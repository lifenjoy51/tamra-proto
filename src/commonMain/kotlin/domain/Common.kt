package domain

import com.soywiz.korma.geom.Point

data class TXY(
    val x: Int,
    val y: Int
) {
    val crossXY get() = listOf(TXY(x, y), TXY(x - 1, y), TXY(x + 1, y), TXY(x, y - 1), TXY(x, y + 1))
    fun toXY(tileSize: Int): Point = Point((x * tileSize).toDouble(), (y * tileSize).toDouble())
}

fun Point.toTXY(tileSize: Int): TXY = TXY((x / tileSize).toInt(), (y / tileSize).toInt())

operator fun Point.plus(other: Point) = Point(this.x + other.x, this.y + other.y)