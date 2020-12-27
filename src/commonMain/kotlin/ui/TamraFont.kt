package ui

import com.soywiz.korge.view.Container
import com.soywiz.korge.view.Text
import com.soywiz.korge.view.ViewDslMarker
import com.soywiz.korge.view.addTo
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.font.TtfFont
import com.soywiz.korim.text.DefaultStringTextRenderer
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korim.text.TextRenderer

class TamraFont(
    val font: TtfFont
) {
    companion object {
        private lateinit var instance: TamraFont
        fun init(font: TtfFont) {
            instance = TamraFont(font)
        }
        fun get(): TtfFont = instance.font
    }
}

inline fun Container.tamraText(
    text: String,
    textSize: Double = Text.DEFAULT_TEXT_SIZE,
    color: RGBA = Colors.WHITE,
    alignment: TextAlignment = TextAlignment.TOP_LEFT,
    renderer: TextRenderer<String> = DefaultStringTextRenderer,
    autoScaling: Boolean = true,
    block: @ViewDslMarker Text.() -> Unit = {}
): Text = Text(text, textSize, color, TamraFont.get(), alignment, renderer, autoScaling)
    .addTo(this, block)
