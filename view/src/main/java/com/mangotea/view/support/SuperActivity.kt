package com.mangotea.view.support

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mangotea.rely.d
import org.jetbrains.anko.internals.AnkoInternals

open class SuperActivity : AppCompatActivity() {

    private val _clickListener by lazy { View.OnClickListener { v -> onClickView(v) } }

    open fun onClickView(v: View) {}

    fun <T : View> T.toClick(): T {
        this.setOnClickListener(_clickListener)
        return this
    }

    val resultMap by lazy { hashMapOf<Int, (Int, Intent?) -> Unit>() }

    private val permissionResults by lazy { hashMapOf<Int, (Int, Array<String>, IntArray) -> Unit>() }

    fun permissionResult(code: Int, block: (Int, Array<String>, IntArray) -> Unit) {
        permissionResults[code] = block
    }

    inline fun <reified T : Activity> startActivityForResult(
            vararg params: Pair<String, Any?>,
            noinline blo: (Int, Intent?) -> Unit
    ) {
        val requestCode: Int = androidx.core.view.ViewCompat.generateViewId()
        this.resultMap[requestCode] = blo
        AnkoInternals.internalStartActivityForResult(this, T::class.java, requestCode, params)
    }

    fun startActivityForResult(
            intent: Intent,
            requestCode: Int = androidx.core.view.ViewCompat.generateViewId(),
            blo: (Int, Intent?) -> Unit
    ) {
        this.resultMap[requestCode] = blo
        startActivityForResult(intent, requestCode)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        d("$requestCode - - $permissionResults", "IPFS")
        permissionResults[requestCode]?.invoke(requestCode, permissions, grantResults)
        permissionResults.remove(requestCode)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        resultMap[requestCode]?.invoke(resultCode, data)
    }

    override fun onDestroy() {
        resultMap.clear()
        d("${this::class.java.name}", "onDestory")
        super.onDestroy()
    }
}
