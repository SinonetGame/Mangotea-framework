package com.mangotea.view.component.drawer

import android.content.Context
import android.view.Gravity.*
import android.view.View
import com.mangotea.rely.d
import com.mangotea.rely.screenWidth
import com.mangotea.view.R
import com.mangotea.view.parentId
import com.mangotea.view.show
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko.constraint.layout._ConstraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.dip
import org.jetbrains.anko.findOptional

open class TelescopicLayout(context: Context) :
    _ConstraintLayout(context) {
    private var _panel: View? = null
    private var _handle: View? = null

    val panel
        get() = _panel
    val handle
        get() = _handle

    init {
        AnkoContext.createDelegate(this)
    }

    var handleLength = dip(50)
        set(value) {
            field = value
            resetLayout()
        }
    open var outstretch = false
        set(value) {
            field = value
            resetLayout()
        }

    var handlePosition: Int = HandlePosition.LEFT
        set(value) {
            field = value
            resetLayout()
        }
    val orientation: Int
        get() = if (handlePosition == HandlePosition.TOP || handlePosition == HandlePosition.BOTTOM) VERTICAL else HORIZONTAL

    var panelLength: Int = (if (orientation == HORIZONTAL) width else height).takeIf { it > 0 } ?: context.screenWidth
        set(value) {
            field = value
            resetLayout()
        }


    private fun resetLayout() {
        _handle?.show = outstretch
        when (handlePosition) {
            HandlePosition.TOP -> {
                _handle?.lparams(matchConstraint, handleLength) {
                    startToStart = parentId
                    endToEnd = parentId
                    topToTop = parentId
                }
                _panel?.lparams(matchConstraint, panelLength) {
                    startToStart = parentId
                    endToEnd = parentId

                    if (outstretch)
                        topToBottom = handleId
                    else
                        topToTop = parentId
                }
            }
            HandlePosition.BOTTOM -> {
                _handle?.lparams(matchConstraint, handleLength) {
                    startToStart = parentId
                    endToEnd = parentId

                    bottomToBottom = parentId
                }
                _panel?.lparams(matchConstraint, panelLength) {
                    startToStart = parentId
                    endToEnd = parentId

                    if (outstretch)
                        bottomToTop = handleId
                    else
                        bottomToBottom = parentId
                }
            }
            HandlePosition.LEFT -> {
                d("panelLength = $panelLength")
                _handle?.lparams(handleLength, matchConstraint) {
                    topToTop = parentId
                    bottomToBottom = parentId
                    startToStart = parentId
                }
                _panel?.lparams(panelLength, matchConstraint) {
                    topToTop = parentId
                    bottomToBottom = parentId

                    if (outstretch)
                        startToEnd = handleId
                    else
                        startToStart = parentId
                }
            }
            HandlePosition.RIGHT -> {
                _handle?.lparams(handleLength, matchConstraint) {
                    topToTop = parentId
                    bottomToBottom = parentId

                    endToEnd = parentId
                }
                _panel?.lparams(panelLength, matchConstraint) {
                    topToTop = parentId
                    bottomToBottom = parentId

                    if (outstretch)
                        endToStart = handleId
                    else
                        endToEnd = parentId
                }
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed)
            resetLayout()
    }


    fun handle(init: (@AnkoViewDslMarker _ConstraintLayout).() -> View) {
        _handle = init()
        _handle?.id = handleId
    }

    fun handle(view: View) {
        findOptional<View>(handleId)?.let {
            removeView(it)
        }
        view.id = handleId
        _handle = view
        findOptional<View>(handleId) ?: addView(_handle)
    }

    fun panel(init: (@AnkoViewDslMarker _ConstraintLayout).() -> View) {
        _panel = init()
        _panel?.id = panelId
    }

    fun panel(view: View) {
        findOptional<View>(panelId)?.let {
            removeView(it)
        }
        view.id = panelId
        _panel = view
        findOptional<View>(panelId) ?: addView(_panel)
    }

    val panelId = R.id.panelId
    val handleId = R.id.handleId

    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
    }
}

object HandlePosition {
    val TOP = AXIS_PULL_BEFORE or AXIS_SPECIFIED shl AXIS_Y_SHIFT

    /** Push object to the bottom of its container, not changing its size.  */
    val BOTTOM = AXIS_PULL_AFTER or AXIS_SPECIFIED shl AXIS_Y_SHIFT

    /** Push object to the left of its container, not changing its size.  */
    val LEFT = AXIS_PULL_BEFORE or AXIS_SPECIFIED shl AXIS_X_SHIFT

    /** Push object to the right of its container, not changing its size.  */
    val RIGHT = AXIS_PULL_AFTER or AXIS_SPECIFIED shl AXIS_X_SHIFT
}