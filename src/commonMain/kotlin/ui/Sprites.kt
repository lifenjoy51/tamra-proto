package ui

import com.soywiz.korge.view.SpriteAnimation
import com.soywiz.korim.bitmap.Bitmap
import tamra.common.Direction

fun Bitmap.getDirectionSprites(): Map<Direction, SpriteAnimation> {
    return mapOf(
        Direction.DOWN to getSpriteAnimation(0),
        Direction.UP to getSpriteAnimation(1),
        Direction.RIGHT to getSpriteAnimation(2),
        Direction.LEFT to getSpriteAnimation(3),
    )
}

fun Bitmap.getSpriteAnimation(
    size: Int = 8,
    seq: Int = 0,
    col: Int = 2
): SpriteAnimation {
    return SpriteAnimation(
        spriteMap = this,
        spriteWidth = size,
        spriteHeight = size,
        marginTop = seq * 8,
        columns = col
    )
}