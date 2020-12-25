package util

import com.soywiz.korge.tiled.TiledMap
import domain.TXY
import domain.XY

/**
 * 특정 타입의 타일을 레이어에서 로드한다.
 */
fun <T> TiledMap.loadTiles(layerName: String, types: Map<Int, T>): Map<TXY, T> {
    val layer = tileLayers.find { it.name == layerName }!!
    val allPoints = (0 until width).map { x ->
        (0 until height).map { y -> TXY(x, y) }
    }.flatten()
    return allPoints.associateWith { p -> layer[p.x, p.y].let { types[it] }!! }
}

fun TiledMap.getMovableArea(): List<List<Pair<XY, XY>>> {
    val objLayer = objectLayers.find { it.name == "area" }!!
    val movablePolygon = objLayer.objects.map {
        (it.objectType as TiledMap.Object.Type.Polygon).points.map { p ->
            XY(p.x + it.bounds.left, p.y + it.bounds.top)
        }
    }
    return movablePolygon.map { list ->
        list.mapIndexed { i: Int, xy: XY ->
            var t = if (i == 0) list.size - 1 else i - 1
            xy to list[t]
        }
    }
}

fun TiledMap.getObjectNames(layerName: String): Map<TXY, String> {
    return objectLayers.find { it.name == layerName }!!.objects.associate {
        val txy = XY(it.bounds.x, it.bounds.y).toTXY(tilewidth)
        val objName = it.name
        txy to objName
    }
}