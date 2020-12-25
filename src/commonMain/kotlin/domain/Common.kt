package domain

data class TXY(
    val x: Int,
    val y: Int
) {
    val crossXY get() = listOf(TXY(x, y), TXY(x - 1, y), TXY(x + 1, y), TXY(x, y - 1), TXY(x, y + 1))
    fun toXY(tileSize: Int): XY = XY((x * tileSize).toDouble(), (y * tileSize).toDouble())
}

data class XY(
    val x: Double,
    val y: Double
) {
    fun toTXY(tileSize: Int): TXY = TXY((x / tileSize).toInt(), (y / tileSize).toInt())
}

operator fun XY.plus(other: XY) = XY(this.x + other.x, this.y + other.y)