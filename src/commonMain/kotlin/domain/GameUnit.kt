package domain

import com.soywiz.korma.geom.Point

abstract class GameUnit {
    abstract var point: Point
    abstract var v: Double
    abstract var size: Point
    abstract val map: GameMap

    private fun isMovable(point: Point) = map.isMovable(point)

    fun moveUp() {
        val newXy = Point(point.x, point.y - v)
        if (isMovable(newXy)) point = newXy
    }

    fun moveDown() {
        val newXy = Point(point.x, point.y + v)
        if (isMovable(newXy)) point = newXy
    }

    fun moveLeft() {
        val newXy = Point(point.x - v, point.y)
        if (isMovable(newXy)) point = newXy
    }

    fun moveRight() {
        val newXy = Point(point.x + v, point.y)
        if (isMovable(newXy)) point = newXy
    }

    fun move(dx: Double, dy: Double): Boolean {
        val newXy = Point(point.x + dx, point.y + dy)
        val moved = isMovable(newXy)
        if (moved) point = newXy
        return moved
    }

}