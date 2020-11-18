package com.mangotea.view.component.header

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout
import com.mangotea.rely.Pre
import com.mangotea.view.*
import com.mangotea.view.component.header.simple.TextHeaderBlock
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.constraint.layout._ConstraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.custom.customView
import org.jetbrains.anko.find
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.wrapContent


/**
 * Created by dydyt on 2016/12/21.
 */
class HeaderView : _ConstraintLayout {
    private var _title: HeaderBlock? = null
    private var _left: HeaderBlock? = null
    private var _right: HeaderBlock? = null
    private val _statusBar: StatusBarView

    val leftParrent: LinearLayout
    val rightParrent: LinearLayout
    val titleParrent: LinearLayout


    constructor(
        context: Context,
        leftWeight: Float = 1f,
        centerWeight: Float = 2f,
        rightWeight: Float = 1f
    ) : super(
        context
    ) {
        AnkoContext.createDelegate(this).apply {
            _statusBar = customView<StatusBarView> {
                id = R.id.statusBarId
            }.lparams(matchConstraint, matchConstraint) {
                startToStart = parentId
                endToEnd = parentId
                topToTop = parentId
            }
            leftParrent = linearLayout {
                id = R.id.leftParrent
                gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
            }.lparams(matchConstraint, matchConstraint) {
                topToBottom = R.id.statusBarId
                bottomToBottom = parentId

                startToStart = parentId
                endToStart = R.id.titleParrent
                horizontalWeight = leftWeight
            }

            titleParrent = linearLayout {
                id = R.id.titleParrent
                gravity = Gravity.CENTER
            }.lparams(matchConstraint, wrapContent) {
                topToBottom = R.id.statusBarId
                bottomToBottom = parentId

                startToEnd = R.id.leftParrent
                endToStart = R.id.rightParrent
                horizontalWeight = centerWeight
            }

            rightParrent = linearLayout {
                id = R.id.rightParrent
                gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            }.lparams(matchConstraint, matchConstraint) {
                topToBottom = R.id.statusBarId
                bottomToBottom = R.id.titleParrent

                startToEnd = R.id.titleParrent
                endToEnd = parentId
                horizontalWeight = rightWeight
            }
        }
    }


    var leftView: HeaderBlock
        get() = _left ?: TextHeaderBlock(context).apply {
            type = HeaderBlock.LEFT
            setView(0, this)
        }
        set(value) {
            if (value is View) {
                value.type = HeaderBlock.LEFT
                setView(0, value)
            } else
                throw RuntimeException("_left is not instanceof View")
        }

    var left
        get() = leftView.getText()
        set(value) {
            leftView.setText(value)
        }

    private var _leftResource = 0
    var leftResource
        get() = _leftResource
        set(value) {
            _leftResource = value
            leftView.setText(value)
        }

    private var _leftColor = 0
    var leftColor
        get() = _leftColor
        set(value) {
            _leftColor = value
            leftView.setTextColor(value)
        }

    var leftIcon
        get() = leftView.icon
        set(value) {
            leftView.icon = value
        }

    private var _leftIconResource = 0
    var leftIconResource: Int
        get() = _leftIconResource
        set(value) {
            _leftIconResource = value
            leftView.icon = drawable(value)
        }

    var leftSize
        get() = leftView.getTextSize()
        set(value) {
            leftView.setTextSize(value)
        }

    fun leftClick(value: Pre<View>) {
        leftView.setOnClickListener(OnClickListener {
            value(it)
        })
    }

    var rightView: HeaderBlock
        get() = _right ?: TextHeaderBlock(context).apply {
            type = HeaderBlock.RIGHT
            setView(1, this)
        }
        set(value) {
            if (value is View) {
                value.type = HeaderBlock.RIGHT
                setView(1, value)
            } else
                throw RuntimeException("_right is not instanceof View")
        }

    var right
        get() = rightView.getText()
        set(value) {
            rightView.setText(value)
        }

    private var _rightResource = 0
    var rightResource
        get() = _rightResource
        set(value) {
            _rightResource = value
            rightView.setText(value)
        }

    private var _rightColor = 0
    var rightColor
        get() = _rightColor
        set(value) {
            _rightColor = value
            rightView.setTextColor(value)
        }

    var rightIcon
        get() = rightView.icon
        set(value) {
            rightView.icon = value
        }

    private var _rightIconResource = 0
    var rightIconResource: Int
        get() = _rightIconResource
        set(value) {
            _rightIconResource = value
            rightView.icon = drawable(value)
        }

    var rightSize
        get() = rightView.getTextSize()
        set(value) {
            rightView.setTextSize(value)
        }

    fun rightClick(value: Pre<View>) {
        rightView.setOnClickListener(OnClickListener {
            value(it)
        })
    }


    var titleView: HeaderBlock
        get() = _title ?: TextHeaderBlock(context).apply {
            setView(-1, this)
        }
        set(value) {
            if (value is View) {
                value.type = HeaderBlock.CENTER
                setView(-1, value)
            } else
                throw RuntimeException("_title is not instanceof View")
        }

    var title
        get() = titleView.getText()
        set(value) {
            titleView.setText(value)
        }

    private var _titleResource = 0
    var titleResource
        get() = _titleResource
        set(value) {
            _titleResource = value
            titleView.setText(value)
        }

    private var _titleColor = 0
    var titleColor
        get() = _titleColor
        set(value) {
            _titleColor = value
            titleView.setTextColor(value)
        }

    var titleIcon
        get() = titleView.icon
        set(value) {
            titleView.icon = value
        }

    private var _titleIconResource = 0
    var titleIconResource: Int
        get() = _titleIconResource
        set(value) {
            _titleIconResource = value
            titleView.icon = drawable(value)
        }

    var titleSize
        get() = titleView.getTextSize()
        set(value) {
            titleView.setTextSize(value)
        }

    private var _titleClick: Pre<View>? = null
    var titleClick: Pre<View>?
        get() = _titleClick
        set(value) {
            if (value != null) {
                _titleClick = value
                titleView.setOnClickListener(OnClickListener {
                    value(it)
                })
            }
        }

    private fun setView(type: Int, view: View) {
        val parrent = find<ViewGroup>(
            when (type) {
                0 -> R.id.leftParrent
                1 -> R.id.rightParrent
                else -> R.id.titleParrent
            }
        )
        if (parrent.indexOfChild(view) >= 0)
            return
        if (parrent.childCount > 0)
            parrent.removeAllViews()
        parrent.addView(view)
        when (type) {
            0 -> this._left = view as HeaderBlock
            1 -> this._right = view as HeaderBlock
            else -> this._title = view as HeaderBlock
        }
    }

    //该属性相关的方法或变量必须在view绘制完成后调用
    fun setImmersive(statusBarTextColor: Int? = null, dark: Boolean? = null) {
        if (dark != null) {
            _statusBar.setPermeateStyle(statusBarTextColor, dark)
        } else {
            _statusBar.setPermeateStyle(statusBarTextColor, !isLightColor(titleColor))
        }
        _statusBar.visibility = View.VISIBLE
        layoutParams.height = defaultHeight + statusBarHeight
    }

    var defaultHeight = -178921
        get() = if (field == -178921) layoutParams.height else field

    fun dismisStatusBar() {
        _statusBar.visibility = View.GONE
    }

}
