package util

import com.soywiz.korge.tiled.TiledMap
import tamra.common.*

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

fun TiledMap.getObjectNames(layerName: String): Map<TileXY, String> {
    return objectLayers.find { it.name == layerName }!!.objects.associate {
        val txy = LocationXY.fromMap(it.bounds.x, it.bounds.y).toWorldTileXY()
        val objName = it.name
        txy to objName
    }
}

fun TiledMap.getTiles(): Map<TileXY, TileId> {
    return data.tileLayers.find { it.name == "terrain" }!!.let {
        val m: MutableMap<TileXY, TileId> = mutableMapOf()
        for (x in 0 until it.width) {
            for (y in 0 until it.height) {
                m[TileXY(x, y)] = TileId(it[x, y])
            }
        }
        m
    }
}

fun TiledMap.getCollisions(): Map<TileId, List<PointArea>> {
    val tileSet = tilesets.first().data
    return tileSet.tiles.associate { tileData ->
        // 2차원 배열.
        val matrix = tileData.objectGroup?.objects?.map {
            (it.objectType as TiledMap.Object.Type.Polygon).points.map { p ->
                PointXY(p.x + it.bounds.left, p.y + it.bounds.top)
            }
        }
        // point area
        val areaList = matrix?.map { l ->
            val area = l.mapIndexed { i: Int, point: PointXY ->
                var t = if (i == 0) l.size - 1 else i - 1
                PointLine(point, l[t])
            }
            PointArea(area)
        } ?: emptyList()
        // id to areaList
        TileId(tileData.id + 1) to areaList
    }
}