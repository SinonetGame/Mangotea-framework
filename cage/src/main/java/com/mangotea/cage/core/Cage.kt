package com.mangotea.cage.core

interface Cage {
    val TAG: String
        get() = "CAGELOG"

    var version: Int

    /***************基础存储操作，针对单一对象。无论对象为何类型、是否为集合*************/
    /**
     * 保存任意对象数据
     * 如果[key]相同则会覆盖
     * @param key 键
     * @param value 要保存的对象数据
     * @return 保存成功或失败
     */
    operator fun <T> set(key: String, value: T): Boolean

    /**
     * 读取指定[key]值对应的对象数据
     * @param key 键
     * @return 对象数据
     */
    operator fun <T> get(key: String): T?

    /**
     * 读取指定[key]值对应的对象数据
     * @param key 键
     * @param block 根据[key]找到的对应对象数据的域函数对象，如果[block]函数块返回结果不为空，则会调用该域方法
     * @return 对象数据
     */
    fun <T> use(key: String, block: T.() -> Unit)

    /**
     * 删除[key]值对应的对象数据
     * @param key 键
     * @return 删除成功或失败
     */
    fun delete(key: String): Boolean

    /**
     * 修改指定[key]的对象数据
     * @param key 键
     * @param edit 根据[key]找到的对应对象数据的域函数对象，如果[edit]函数块返回结果不为空，则用它替换掉原本的值
     * @return 修改成功则返回修改后的数据对象，失败或不存在则反馈空
     */
    fun <T> edit(key: String, edit: T.() -> T?): T?

    /**
     * 返回[Cage]中是否包含指定的[key]
     * @param key 键
     * @return 是否包含
     */
    fun contains(key: String): Boolean
    /*************************************end****************************************/

    /**
     * 保存任意对象数据到集合中去
     * [key]相同时，会追加一条新的数据到[Cage]中
     * 该方法允许相同的[key]传递不同类型的[value]，但与之对应的，在通过 [find]或者[finds]等方法来查找这些数据时，需保证调用这些方法的泛型是这些数据对象的共同父类型
     * @param key 键
     * @param value 要添加的对象数据
     * @return 添加成功或失败
     */
    fun <T> add(key: String, value: T): Boolean

    /**
     * 保存批量保存任意对象数据到集合中去 @see [add]
     * @param key 键
     * @param values 要添加的对象数据集合
     * @return 添加成功或失败
     */
    fun <T> adds(key: String, values: Iterable<T>): Boolean

    /**
     * 读取 [key] 对应的集合中的某一条数据
     * @param key 键
     * @return 对象数据
     */
    fun <T> one(key: String): T?

    /**
     * 读取 [key] 对应的集合
     * @param key 键
     * @return 对象数据集合
     */
    fun <T> all(key: String): ArrayList<T>

    /**
     * 读取 [key] 对应的集合
     * @param key 键
     * @param block 查询条件
     * @return 对象数据
     */
    fun <T> each(key: String, block: (T) -> Unit)

    /**
     *  读取 [key] 对应的集合中，满足条件[where]的第一条数据
     * @param key 键
     * @param where 查询条件
     * @return
     */
    fun <T> find(key: String, where: (T) -> Boolean): T?

    /**
     *  读取 [key] 对应的集合的所有数据
     * @param key 键
     * @return
     */
    fun <T> finds(key: String): ArrayList<T>

    /**
     *  读取 [key] 对应的集合中，满足条件[where]的所有数据
     * @param key 键
     * @param where 查询条件
     * @return
     */
    fun <T> finds(key: String, where: (T) -> Boolean): ArrayList<T>

    /**
     *  读取 [key] 对应的集合中，满足条件[where]的第一条数据，如果[where]函数块返回结果不为空，则用它替换掉原本的值
     * @param key 键
     * @param where 查询条件
     * @return
     */
    fun <T> replace(key: String, where: (T) -> T?): Boolean

    /**
     *  读取 [key] 对应的集合中，满足条件[where]的第一条数据，如果[where]函数块返回结果true，则用[value]替换掉原本的值，如果整个集合中都没找到符合该条件的值，则将[value]添加到集合
     * @param key 键
     * @param value 值
     * @param where 查询条件
     * @return
     */
    fun <T> replaceOrAdd(key: String, value: T, where: (T) -> Boolean): Boolean

    fun <T> updateOrAdd(key: String, new: T, where: (T) -> T?): Boolean

    /**
     *  读取 [key] 对应的集合中，满足条件[where]的所有数据，如果[where]函数块返回结果不为空，则用它替换掉原本的值
     * @param key 键
     * @param where 查询条件
     * @return
     */
    fun <T> replaces(key: String, where: (T) -> T?): Boolean

    /**
     *  移除 [key] 对应的集合中，满足条件[where]的第一条数据
     * @param key 键
     * @param where 查询条件
     * @return
     */
    fun <T> remove(key: String, where: (T) -> Boolean): Boolean

    /**
     *  移除 [key] 对应的集合的所有数据
     * @param key 键
     * @return
     */
    fun removes(key: String): Boolean

    /**
     *  移除 [key] 对应的集合中，满足条件[where]的所有数据
     * @param key 键
     * @param where 查询条件
     * @return
     */
    fun <T> removes(key: String, where: (T) -> Boolean): Boolean

    /**
     *  判断 [key] 对应的数据是否为Cage集合 *Cage集合本质上只是一种抽象的数据结构，并不一定是代码中的[kotlin.collections.Iterable]等对象
     * @param key 键
     * @return
     */
    fun isPile(key: String): Boolean

    /**
     *  判断 [key] 对应的数据是否为Cage集合 且该集合是否包含数据。
     * @param key 键
     * @return
     */
    fun pileContains(key: String): Boolean

    /**
     * 清除指定key的元数据或者数据集合
     */
    fun clean(key: String): Boolean

    /**
     * 清除所有已保存的数据
     */
    fun clean(): Boolean
}