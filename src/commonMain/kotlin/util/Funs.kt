package util

import com.soywiz.korge.view.Sprite
import domain.XY

fun Sprite.xy(): XY {
    return XY(
        x + width / 2,
        y + height / 2
    )
}