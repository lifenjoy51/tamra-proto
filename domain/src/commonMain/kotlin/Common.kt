package domain

import kotlin.math.absoluteValue

val baseCoord = Coord(126.1, -34.05)
val tileSize = 8
val tilesPerY = 120
val tilesPerX = 100

// 타일 상의 좌표.
data class TileXY(
    val x: Int,
    val y: Int
) {
    val crossXY get() = listOf(TileXY(x, y), TileXY(x - 1, y), TileXY(x + 1, y), TileXY(x, y - 1), TileXY(x, y + 1))
    fun toLocationXY(): LocationXY = LocationXY((x * tileSize).toDouble(), (y * tileSize).toDouble())
}

fun LocationXY.toTileXY(): TileXY = TileXY((x - baseCoord.point.x).toInt() / tileSize, (y - baseCoord.point.y).toInt() / tileSize)

operator fun LocationXY.plus(other: LocationXY) = LocationXY(this.x + other.x, this.y + other.y)

// 좌표.
// +E/-W +S/-N
data class Coord(
    val x: Double,
    val y: Double
) {
    val point: LocationXY get() = LocationXY(x * tileSize * tilesPerX, y * tileSize * tilesPerY)
    val tileXy: TileXY get() = point.toTileXY()
}

// 위치정보.
data class LocationXY(
    val x: Double,
    val y: Double
) {
    fun abs(): LocationXY = LocationXY(
        x.absoluteValue,
        y.absoluteValue
    )
}