package com.mangotea.view

import android.app.Activity
import android.app.Dialog
import android.view.View
import androidx.fragment.app.Fragment
import kotlin.reflect.KProperty

class IdView<T : View>(private val id: Int, private val context: Any) {
    constructor(id: Int, view: View) : this(id = id, context = view)
    constructor(id: Int, activity: Activity) : this(id = id, context = activity)
    constructor(id: Int, fragment: Fragment) : this(id = id, context = fragment)
    constructor(id: Int, dialog: Dialog) : this(id = id, context = dialog)

    private var value: T? = null
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: when (context) {
            is View ->
                context.findViewById(id)
            is Activity ->
                context.findViewById(id)
            is Dialog ->
                context.findViewById(id)
            else ->
                (context as? Fragment)?.view?.findViewById(id) as T
        }.apply { value = this }
    }
}

class IdViewOptional<T : View>(private val id: Int, private val context: Any) {
    constructor(id: Int, view: View) : this(id = id, context = view)
    constructor(id: Int, activity: Activity) : this(id = id, context = activity)
    constructor(id: Int, fragment: Fragment) : this(id = id, context = fragment)
    constructor(id: Int, dialog: Dialog) : this(id = id, context = dialog)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return when (context) {
            is View ->
                context.findViewById(id)
            is Activity ->
                context.findViewById(id)
            is Dialog ->
                context.findViewById(id)
            else ->
                (context as? Fragment)?.view?.findViewById(id) as? T
        }
    }
}