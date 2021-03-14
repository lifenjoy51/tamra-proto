package ui

import com.soywiz.korge.view.SpriteAnimation
import com.soywiz.korim.bitmap.Bitmap
import tamra.common.Direction

fun Bitmap.getDirectionSprites(): Map<Direction, SpriteAnimation> {
    return mapOf(
        Direction.DOWN to getSpriteAnimation(seq = 0),
        Direction.UP to getSpriteAnimation(seq = 1),
        Direction.RIGHT to getSpriteAnimation(seq = 2),
        Direction.LEFT to getSpriteAnimation(seq = 3),
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