package com.mangotea.ipfs

import android.content.Context
import android.os.Build
import com.mangotea.http.gson
import com.mangotea.rely.*
import com.mangotea.rely.chain.Chain
import com.mangotea.rely.chain.chain
import com.mangotea.rely.chain.end
import com.mangotea.rely.chain.then
import com.mangotea.rely.net.NETWORK_NO
import com.mangotea.rely.net.netWorkType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InterruptedIOException
import java.net.ConnectException
import java.nio.charset.Charset
import java.util.*

class Daemon(_context: Context) {
    companion object {
        protected const val TAG = "IPFS-Core"
    }

    private val daemon = IpfsDaemon(_context)
    private var lastTask: Chain<Unit>? = null
    private var onDone: (() -> Unit)? = null
    private var onFailed: ((Throwable) -> Unit)? = null
    val working
        get() = daemon.working

    var starting = false
        private set

    fun destory() {
        starting = false
        lastTask?.cancel()
        lastTask?.destory()
        daemon.daemonProcess?.destroy()
        lastTask = null
    }

    fun root(rootDir: File): Daemon {
        if (!rootDir.exists() || !rootDir.isDirectory)
            throw DaemonException("IPFS rootDir must be a directory!")
        daemon.rootDir = rootDir
        return this
    }

    fun repo(repoDir: File): Daemon {
        if (repoDir.exists() && repoDir.isDirectory) {
            daemon.repoDir = repoDir
        }
        return this
    }

    fun apiPort(port: Int): Daemon {
        apiPort = port
        return this
    }

    fun swarmPort(port: Int): Daemon {
        swarmPort = port
        return this
    }

    fun gatewayPort(port: Int): Daemon {
        gatewayPort = port
        return this
    }

    fun newestVersion(version: String): Daemon {
        daemon.newstVersion = version
        return this
    }

    fun swarmkey(key: String): Daemon {
        daemon.swarmKey = key
        return this
    }

    fun privateKey(key: String): Daemon {
        daemon.privateKeyStr = key
        return this
    }

    fun publicKey(key: String): Daemon {
        daemon.publicKeyStr = key
        return this
    }

    fun bootstraps(bootstraps: ArrayList<String>): Daemon {
        daemon.bootstraps = bootstraps
        return this
    }

    fun started(block: () -> Unit): Daemon {
        onDone = block
        return this
    }

    fun failed(block: (err: Throwable) -> Unit): Daemon {
        onFailed = block
        return this
    }

    fun init(frontMode: String? = null, behindMode: String? = null) {
        destory()
        chain {
            starting = true
            daemon.start(frontMode, behindMode)
        }.then {
            var cmds: String? = null
            while (cmds.isNullOrEmpty()) {
                cmds = try {
                    ipfs.diagCmds().string()
                } catch (e: Throwable) {
                    if (e !is ConnectException)
                        w(e, TAG)
                    null
                }
                delay(200)
            }
            if (!cmds.isNullOrEmpty()) {
                d("Daemon is Ready ", TAG)
            }
        }.end {
            d("Daemon will to use ", TAG)
            async(mainCoroutine) { onDone?.invoke() }.await()
        }.onFailure { chain, throwable ->
            async(mainCoroutine) { onFailed?.invoke(throwable) }
        }.onFinally {
            starting = false
        }.apply {
            lastTask = this
            call()
        }
    }

    private class IpfsDaemon(private val context: Context) {
        lateinit var rootDir: File
        var repoDir: File? = null
        var swarmKey: String? = null
        var bootstraps: ArrayList<String>? = null
        var privateKeyStr: String? = null
        var publicKeyStr: String? = null
        var newstVersion: String? = null

        private val binaryFile by lazy { File(rootDir, "ipfsbin") }

        //        private val repoPath by lazy { File(rootDir, "ipfs_repo") }
        private val repoPath
            get() = (repoDir ?: File(rootDir, "ipfs_repo")).apply {
                if (!exists())
                    mkdir()
            }

        private val version
            get() = File(repoPath, "version")
        private val swarmKeyFile by lazy { File(repoPath, "swarm.key") }

        private val keysDir by lazy {
            File("$repoPath/keys").apply {
                if (!exists())
                    mkdir()
            }
        }
        private val privateKey by lazy { File(keysDir, "private_key.pem") }
        private val publicKey by lazy { File(keysDir, "public_key.pem") }
        private val log by lazy { File(rootDir, "ipfs.log") }


        var initialized = false
        var initializing = false
        var daemonProcess: Process? = null
            private set

        private val exMsgs by lazy {
            arrayListOf("cannot connect to the api. Is the deamon running?",
                "try running 'ipfs daemon' first",
                "cannot acquire lock: Lock FcntlFlock of",
//                "this action must be run in online mode",
                "no IPFS repo found in",
                "please run: 'ipfs init'")
        }
        val working: Boolean
            get() {
                return try {
                    val cmd = "swarm peers"
                    val exec = cmd(cmd, false)
                    var error = logStream(exec.errorStream, cmd, "error", false)
                    val output = logStream(exec.inputStream, cmd, "input", false)
                    var working = if (context.netWorkType == NETWORK_NO)
                        error.isNullOrEmpty() && output.isNullOrEmpty()
                    else {
                        exMsgs.find { error.contains(it) || output.contains(it) }?.let { false } ?: true
                    }
                    working.d("IPFS-isWorking")
                    if (!working) {
                        "error msg: $error".d("IPFS-isWorking")
                        "output msg: $output".d("IPFS-isWorking")
                    }
                    working
                } catch (e: Throwable) {
                    e.printStackTrace()
                    false
                }
            }

        fun init() {
            if (initializing)
                return
            initialized = false
            initializing = true
            val exists = binaryFile.exists()
            var update = false
            try {
                if (exists) {
                    if (version.exists()) {
                        if (readString(version) != "10") {
                            version.writeText("10")
                            "update version".d(TAG)
                        }
                    }
                    update = checkUpdate()
                    run("repo fsck")
                    //原本已经安装的旧用户
                    "config repo with update[$update]".d(TAG)
                    if (!File(repoPath, "config").exists()) {//如果此次配置的repo文件没有被创建
                        initialize()//重新初始化
                    }

                }
                if (!exists || update) {//如果该文件不存在，则表示没有安装 或者已存在，但是需要更新
                    install()//开始安装
                    initialize()
                }
            } catch (e: Throwable) {
                w(e, "IPFS-Core-INIT-ERROR")
                rootDir.delete()//期间如果出现任何异常，则删除所有安装或初始化生成的文件
//                    throw InitializationException(e.message)//同时抛出一个ipfs初始化的异常
            }
            if (!swarmKey.isNullOrEmpty()) {//如果配置了swarmkey
                if (swarmKeyFile.exists()) {//则判断是否有老旧的swarmkey
                    //如果存在老旧的swarkey则读取key文件并判断key值是否一致，如果不一致，则重新写入新key覆盖
                    if (readString(swarmKeyFile) != swarmKey) {
                        swarmKeyFile.writeText(swarmKey!!)
                        "update swarmkey".d(TAG)
                    }
                } else //如果不存在则直接写入
                {
                    swarmKeyFile.writeText(swarmKey!!)
                    "update swarmkey".d(TAG)
                }
            }
            bootstraps?.takeIf { it.isNotEmpty() }
                ?.let { newBootstraps ->//如果配置了bootstrap，
                    try {
                        val cmd = "bootstrap list"
                        val oldBootstraps = logStream(
                            run(cmd, false).inputStream,
                            cmd,
                            "input",
                            false
                        ).split("\n")
                        //则判断是否已经添加了所有新节点，如果没有，则添加。此处的逻辑主要用于防止安装或更新时，已经配置过bootstrap而进行二次调用
                        if (!oldBootstraps.containsAll(newBootstraps)) {
                            "重新配置bootstrap".d(TAG)
                            run("bootstrap rm all", false)
                            for (bootstrap in newBootstraps) {
                                run("bootstrap add $bootstrap", false)
                            }
                        }
                    } catch (e: ConnectionResetException) {
                        //网络连接断开等某些原因导致socket断开而无法继续读流等异常，该异常不应当影响初始化
                    }
                }

            kotlin.runCatching {
                val newestVersion =
                    logStream(
                        run("version --enc json", false).inputStream,
                        "version --enc json",
                        "input"
                    )
                d("NEWEST IPFS VERSION:$newestVersion", TAG)
            }
            initializing = false
        }

        private fun initialize() {
            "install files".d(TAG)
            run("-c ${repoPath.absolutePath}")//则将其配置为IPFS资源路径
            run("init")//完成安装后执行初始化命令
            "run init cmd".d(TAG)

            if (apiPort > 0 && apiPort != 5001)
                run("config Addresses.API /ip4/127.0.0.1/tcp/$apiPort")
            run("config Addresses.Gateway /ip4/0.0.0.0/tcp/$gatewayPort")
            run("config --json Addresses.Swarm [\"/ip4/0.0.0.0/tcp/$swarmPort\",\"/ip6/::/tcp/$swarmPort\",\"/ip4/0.0.0.0/udp/$swarmPort/quic\",\"/ip6/::/udp/$swarmPort/quic\"]")

            if (!bootstraps.isNullOrEmpty()) {//如果配置了bootstrap，则移除原有所有节点，逐条加入
                run("bootstrap rm all", false)
                for (bootstrap in bootstraps!!) {
                    run("bootstrap add $bootstrap", false)
                }
            }
            initialized = true
        }


        fun start(frontMode: String? = null, behindMode: String? = null) {
            if (!initialized && !initializing)
                init()
            if (daemonProcess != null) {
                daemonProcess?.destroy()
                daemonProcess = null
            }
            val behind = if (!behindMode.isNullOrEmpty()) " $behindMode" else ""
            val front = if (!frontMode.isNullOrEmpty()) " $frontMode " else ""
            val cmd = "${front}daemon$behind"
            daemonProcess = daemonCmd(cmd)
        }


        private fun install() {
            val abi = when {
                Build.CPU_ABI.toLowerCase().startsWith("arm") -> "arm"
                else -> throw  CompatibilityException("Unsupported CPU")
            }
            val source = context.assets.open(abi).source().buffer()

            val sink = binaryFile.sink().buffer()
            while (!source.exhausted()) {
                source.read(sink.buffer(), 1024)
            }
            source.close()
            sink.close()
            binaryFile.setExecutable(true)

            if (!repoPath.exists() && !repoPath.mkdirs())
                throw DaemonException("init keys dir failed")
            if (!keysDir.exists() && !keysDir.mkdirs())
                throw DaemonException("init keys dir failed")
            privateKeyStr?.let {
                privateKey.writeText(it)
            }
            publicKeyStr?.let {
                publicKey.writeText(it)
            }
        }

        private fun checkUpdate(): Boolean {
            d("IPFS NEW VERSION:$newstVersion", TAG)
            if (newstVersion.isNullOrEmpty())
                return true
            val installedVersion =
                logStream(
                    run("version --enc json", false).inputStream,
                    "version --enc json",
                    "input"
                ).takeIf { !it.isNullOrEmpty() }?.let {
                    runCatching {
                        d("IPFS INSTALLED VERSION:$it", TAG)
                        gson.fromJson(it, Version::class.java)
                    }.getOrNull()
                }?.Sn ?: return true
            return SinoVersion.of(newstVersion) > installedVersion
        }

        private fun run(cmd: String, output: Boolean = true): Process {
            val exec = cmd(cmd, output)
            var error = logStream(exec.errorStream, cmd, "error", output)
            if (output)
                logStream(exec.inputStream, cmd, "input")
            if (exec.waitFor() != 0) {
                filterError(cmd, error)
            }
            return exec
        }

        private fun daemonCmd(cmd: String): Process {
            val exec = cmd(cmd)
            d("daemon:$exec")
            GlobalScope.async(childCoroutine) {
                try {
                    val code = exec.waitFor()
                    if (code != 0) {
                        val error = logStream(exec.errorStream, cmd, "error")
                        val isError = filterError(cmd, error, false)
                        if (isError) {
                            w("IPFS daemon process mabe terminated!  code:$code  error:$error", TAG)
                        }
                    } else {
                        val input = logStream(exec.inputStream, cmd, "input")
                        w("IPFS daemon process mabe terminated!  code:$code  input:$input", TAG)
                    }
                } catch (e: Throwable) {
                    w(e?.message ?: "catch null error message", TAG)
                    w(e, TAG)
                }
            }
            return exec
        }

        private fun cmd(cmd: String, pr: Boolean = true): Process {
            if (!binaryFile.exists())
                throw DaemonException("IPFS Uninstall")
            val env =
                arrayOf("IPFS_PATH=${repoPath.absoluteFile}", "GOLOG_FILE=${log.absoluteFile}")
            val command = binaryFile.absolutePath + " " + cmd
            val exec = try {
                Runtime.getRuntime().exec(command, env)
            } catch (e: IOException) {
                e.message?.let {
                    if (it.indexOf("error=2, No such file or directory") > 0) {
                        install()
                    }
                }
                e(e)
                Runtime.getRuntime().exec(command, env)
            }
            if (pr)
                d("cmd: [$cmd] executing", TAG)
            return exec
        }

        private fun filterError(cmd: String, error: String, throwable: Boolean = true): Boolean {
            if (error.isEmpty())
                return true
            if (cmd != "repo fsck" && error.indexOf("someone else has the lock") >= 0) {
                return runCatching {
                    run("repo fsck")
                    true
                }.getOrElse { false }
            } else {
                val isError = error.indexOf("ipfs configuration file already exists!") < 0 &&
                        error.indexOf("ipfs daemon is running. please stop it to run this command") < 0
                if (isError) {
                    w("IPFS error:$error", TAG)
                    if (throwable) {
                        if (error.indexOf("connection reset by peer") >= 0)
                            throw ConnectionResetException()
                        else
                            throw DaemonException("IPFS Cmd [$cmd] Exception:\n$error")
                    }
                }
                return isError
            }

        }

        private fun logStream(
            stream: InputStream,
            cmd: String,
            tag: String = "LOG",
            pr: Boolean = true
        ): String {
            if (pr)
                d("logStream", TAG)
            val log = try {
                stream.bufferedReader().readText()
            } catch (e: InterruptedIOException) {
                w(e, TAG)
                "Process read interrupted"
            } catch (e: Exception) {
                w(e, TAG)
                e.message ?: ""
            }

            if (log.isNotEmpty() && pr)
                d("only log: [$cmd] $tag: $log", TAG)
            return log
        }

        private fun readString(file: File): String? {
            val source = file.source().buffer()
            val string = source.readString(Charset.defaultCharset())
            source.close()
            d("read file [${file.absolutePath}]", TAG)
            d(" to String [$string]", TAG)
            return string
        }

    }


}

open class DaemonException(msg: String = "Unknown IPFS Daemon Exception") : RuntimeException(msg)

class CompatibilityException(msg: String = "Unknown Compatibility Exception") : DaemonException(msg)

class ConnectionResetException(msg: String = "connection reset by peer") : DaemonException(msg)

class InitializationException(msg: String?) :
    DaemonException("Initialization of IPFS failed, maybe it is running or has been initialized\nError message:\n$msg")
