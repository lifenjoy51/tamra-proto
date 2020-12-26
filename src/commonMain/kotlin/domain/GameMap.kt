package domain

import util.LineHelper

abstract class GameMap {
    abstract val movableArea: List<List<Pair<XY, XY>>>

    fun isMovable(xy: XY): Boolean {
        val base = (xy to XY(0.0, xy.y))
        // 교점의 수가 홀수이면 안에 위치.
        // https://en.wikipedia.org/wiki/Point_in_polygon
        return movableArea.any { area ->
            area.map {
                if (LineHelper.doIntersect(base, it)) 1 else 0
            }.sum() % 2 == 1
        }
    }
}