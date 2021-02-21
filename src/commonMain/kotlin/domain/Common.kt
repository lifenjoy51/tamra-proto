package domain

import com.soywiz.korma.geom.Point
import tileSize
import tilesPerX
import tilesPerY

// 타일 상의 좌표.
data class TileXY(
    val x: Int,
    val y: Int
) {
    val crossXY get() = listOf(TileXY(x, y), TileXY(x - 1, y), TileXY(x + 1, y), TileXY(x, y - 1), TileXY(x, y + 1))
    fun toXY(): Point = Point((x * tileSize).toDouble(), (y * tileSize).toDouble())
}

fun Point.toTXY(): TileXY = TileXY((x / tileSize).toInt(), (y / tileSize).toInt())

operator fun Point.plus(other: Point) = Point(this.x + other.x, this.y + other.y)

// 좌표.
// +E/-W +S/-N
data class Coord(
    val x: Double,
    val y: Double
) {
    val point: Point get() = Point(x * tileSize * tilesPerX, y * tileSize * tilesPerY)
}