package util

import com.soywiz.korge.tiled.TiledMap
import com.soywiz.korma.geom.Point
import domain.LocationXY
import domain.TileXY
import domain.toTileXY

/**
 * 특정 타입의 타일을 레이어에서 로드한다.
 */
fun <T> TiledMap.loadTiles(layerName: String, types: Map<Int, T>): Map<TileXY, T> {
    val layer = tileLayers.find { it.name == layerName }!!
    val allPoints = (0 until width).map { x ->
        (0 until height).map { y -> TileXY(x, y) }
    }.flatten()
    return allPoints.associateWith { p -> layer[p.x, p.y].let { types[it] }!! }
}

fun TiledMap.getMovableArea(): List<List<Pair<Point, Point>>> {
    return emptyList()
    /*
    val objLayer = objectLayers.find { it.name == "area" }!!
    val movablePolygon = objLayer.objects.map {
        (it.objectType as TiledMap.Object.Type.Polygon).points.map { p ->
            Point(p.x + it.bounds.left, p.y + it.bounds.top)
        }
    }
    return movablePolygon.map { list ->
        list.mapIndexed { i: Int, point: Point ->
            var t = if (i == 0) list.size - 1 else i - 1
            point to list[t]
        }
    }
    */
}

fun TiledMap.getObjectNames(layerName: String): Map<TileXY, String> {
    return objectLayers.find { it.name == layerName }!!.objects.associate {
        val txy = LocationXY(it.bounds.x, it.bounds.y).toTileXY()
        val objName = it.name
        txy to objName
    }
}