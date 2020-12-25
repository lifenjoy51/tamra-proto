package ui

import com.soywiz.korim.font.TtfFont

class TamraFont(
    val font: TtfFont
) {
    companion object {
        lateinit var instance: TamraFont
        fun get(): TtfFont = instance.font
    }
}