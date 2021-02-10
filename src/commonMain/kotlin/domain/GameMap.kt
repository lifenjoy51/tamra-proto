package domain

import com.soywiz.korma.geom.Point
import util.LineHelper

abstract class GameMap {
    abstract val movableArea: List<List<Pair<Point, Point>>>

    fun isMovable(point: Point): Boolean {
        val base = (point to Point(0.0, point.y))
        // 교점의 수가 홀수이면 안에 위치.
        // https://en.wikipedia.org/wiki/Point_in_polygon
        return movableArea.any { area ->
            area.map {
                if (LineHelper.doIntersect(base, it)) 1 else 0
            }.sum() % 2 == 1
        }
    }
}