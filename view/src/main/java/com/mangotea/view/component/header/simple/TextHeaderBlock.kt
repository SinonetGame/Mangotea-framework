package com.mangotea.view.component.header.simple

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView
import com.mangotea.view.component.header.HeaderBlock
import com.mangotea.view.drawableLeft
import com.mangotea.view.drawableRight
import org.jetbrains.anko.lines

@SuppressLint("AppCompatCustomView")
open class TextHeaderBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr), HeaderBlock {

    init {
        lines = 1
        ellipsize = TextUtils.TruncateAt.END
    }

    override var type = HeaderBlock.CENTER

    override var icon: Drawable?
        get() = if (type == HeaderBlock.RIGHT) drawableRight else drawableLeft
        set(value) {
            if (type == HeaderBlock.RIGHT) drawableRight = value else drawableLeft = value
        }

}
