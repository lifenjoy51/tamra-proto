package domain

abstract class GameUnit {
    abstract var xy: XY
    abstract var v: Double
    abstract var size: XY
    abstract val map: GameMap

    private fun isMovable(xy: XY) = map.isMovable(xy + XY(size.x / 2, size.y / 2))

    fun moveUp() {
        val newXy = XY(xy.x, xy.y - v)
        if (isMovable(newXy)) xy = newXy
    }

    fun moveDown() {
        val newXy = XY(xy.x, xy.y + v)
        if (isMovable(newXy)) xy = newXy
    }

    fun moveLeft() {
        val newXy = XY(xy.x - v, xy.y)
        if (isMovable(newXy)) xy = newXy
    }

    fun moveRight() {
        val newXy = XY(xy.x + v, xy.y)
        if (isMovable(newXy)) xy = newXy
    }

}