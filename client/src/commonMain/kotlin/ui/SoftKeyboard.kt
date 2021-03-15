package ui

import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onOut
import com.soywiz.korge.input.onOver
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import tamra.mainHeight
import tamra.mainWidth

const val BACKSPACE = '<'

fun Container.keyboard(block: (c: Char) -> Unit = {}): SoftKeyboard = SoftKeyboard(block).addTo(this)

class SoftKeyboard(block: (c: Char) -> Unit) : Container() {
    private val keyLayout: List<List<Char>> = listOf(
        listOf('1', '2', '3', '4', '5', '6', '7', '8', '9', '0'),
        listOf('q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'),
        listOf('a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', ' '),
        listOf('z', 'x', 'c', 'v', 'b', 'n', 'm', ' ', ' ', BACKSPACE),
    )

    init {
        val size = mainWidth / 10.0
        positionY(mainHeight - size * 4)
        solidRect(mainWidth.toDouble(), size * 4, Colors.BLACK)
        keyLayout.forEachIndexed { row, list ->
            list.forEachIndexed { col, c ->
                val b = solidRect(size, size, color = Colors.TRANSPARENT_BLACK) {
                    position(size * (col), size * (row))
                    alpha = 0.1
                    onOut { alpha = 0.1 }
                    onOver { alpha = 0.5 }
                    onClick {
                        alpha = 0.5
                        block(c)
                    }
                }
                tamraText(c.toString()) {
                    centerOn(b)
                }
                sendChildToFront(b)
            }
        }

    }
}