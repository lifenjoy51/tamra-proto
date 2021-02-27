package tamra.common

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
    fun toLocationXY(): LocationXY = LocationXY((x * tileSize).toDouble(), (y * tileSize).toDouble())
    fun distance(it: TileXY): Int = (x - it.x).absoluteValue + (y - it.y).absoluteValue
}

fun LocationXY.toWorldTileXY(): TileXY = TileXY((x - baseCoord.location.x).toInt() / tileSize, (y - baseCoord.location.y).toInt() / tileSize)
fun LocationXY.toTileXY(): TileXY = TileXY(x.toInt() / tileSize, y.toInt() / tileSize)


operator fun LocationXY.plus(other: LocationXY) = LocationXY(this.x + other.x, this.y + other.y)

// 좌표.
// +E/-W +S/-N
data class Coord(
    val x: Double,
    val y: Double
) {
    val location: LocationXY get() = LocationXY(x * tileSize * tilesPerX, y * tileSize * tilesPerY)
    val tile: TileXY get() = location.toWorldTileXY()
}

// 위치정보.
data class LocationXY(
    val x: Double,
    val y: Double
) {
    companion object {
        fun fromMap(mx: Double, my: Double): LocationXY =
            LocationXY(baseCoord.location.x + mx, baseCoord.location.y + my)
    }

    fun pointXY(): PointXY {
        val ax = (x % tileSize)
        val ay = (y % tileSize)
        return PointXY(
            if (ax > 0) ax else tileSize - ax.absoluteValue,
            if (ay > 0) ay else tileSize - ay.absoluteValue
        )
    }

}

// 위치와 상관 없는 단순 x,y
data class PointXY(
    val x: Double,
    val y: Double
)

// line
data class PointLine(
    val from: PointXY,
    val to: PointXY
)

data class PointArea(
    val lines: List<PointLine>
)

// tileid
data class TileId(
    val id: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as TileId

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}