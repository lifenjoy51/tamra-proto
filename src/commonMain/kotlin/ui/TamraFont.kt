package ui

import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.font.TtfFont
import com.soywiz.korim.text.DefaultStringTextRenderer
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korim.text.TextRenderer
import tamra.defaultMargin

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
    px: Int = defaultMargin,    // x 위치.
    py: Int = defaultMargin,    // y 위치.
    ax: Int = 0,    // 기본 간격으로 부터 x 위치 조정.
    ay: Int = 0,    // 기본 간격으로 부터 y 위치 조정.
    hc: View? = null,  // 컨테이너 기준 수평 중앙정렬.
    vc: View? = null,  // 컨테이너 기준 수직 중앙정렬.
    alignment: TextAlignment = TextAlignment.TOP_LEFT,
    renderer: TextRenderer<String> = DefaultStringTextRenderer,
    autoScaling: Boolean = true,
    block: @ViewDslMarker Text.() -> Unit = {}
): Text = Text(text, textSize, color, TamraFont.get(), alignment, renderer, autoScaling).apply {
    positionX(px + ax)
    positionY(py + ay)
    if (hc != null) {
        alignX(hc, 0.5, true)
    }
    if (vc != null) {
        alignY(vc, 0.5, true)
    }
}.addTo(this, block)
