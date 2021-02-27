package tamra.common

abstract class GameUnit {
    abstract var location: LocationXY
    abstract var velocity: Double
    abstract val map: GameMap

    fun moved(dx: Double = 0.0, dy: Double = 0.0): Boolean {
        val newXy = LocationXY(location.x + dx, location.y + dy)
        val moved = isMovable(newXy)
        if (moved) location = newXy
        return moved
    }

    private fun isMovable(newLocation: LocationXY): Boolean {
        val txy = map.getTileXY(newLocation)
        val collisions = map.collisions[map.tiles[txy]]
        val p1 = newLocation.pointXY()
        val p2 = LocationXY(0.0, newLocation.y).pointXY()
        val base = PointLine(p1, p2)
        // 교점의 수가 홀수이면 안에 위치.
        // https://en.wikipedia.org/wiki/LocationXY_in_polygon
        return (collisions?.any { area ->
            area.lines.map {
                if (LineHelper.doIntersect(base, it)) 1 else 0
            }.sum() % 2 == 1
        } ?: true).xor(map.onWater)
    }
}

enum class Direction {
    DOWN,
    UP,
    RIGHT,
    LEFT,
}