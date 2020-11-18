package com.mangotea.rely.assert

/**
 * 断言表达式
 */
class AssertFunction(private val function: AssertFunction.() -> Boolean) {

    /**
     * 因式断言
     * 判断[factor]的值如果为true，则进行断言，执行[predicate]，并结束代码片段，提前为[function]返回false
     * 如果[factor]的值为false，则不进行断言，直接返回true
     * @param factor 断言表达式
     * @param predicate 断言后分支
     * @return 返回是否正常扭转。当执行断言后，表示非正常扭转，此时返回false，否则返回true
     */
    inline fun assert(factor: Boolean = false, predicate: () -> Unit): Boolean {
        if (factor) {
            predicate()
            throw AssertionException(false)
        }
        return !factor
    }

    operator fun invoke(): Boolean {
        return try {
            function.invoke(this)
        } catch (e: AssertionException) {
            e.next
        }
    }
}


