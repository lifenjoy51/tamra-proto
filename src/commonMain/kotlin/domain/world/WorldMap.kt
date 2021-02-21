package domain.world

import com.soywiz.korge.tiled.TiledMap
import com.soywiz.korma.geom.Point
import domain.GameMap
import domain.LandingId
import domain.Port
import domain.TileXY

class WorldMap(
    override val movableArea: List<List<Pair<Point, Point>>>,
    val portPositions: Map<TileXY, Port?>,
    val landingPositions: Map<TileXY, LandingId?>,
    val tiles: TiledMap.Layer.Tiles,
    val tileCollision: Map<Int, TiledMap.Layer.Objects?>
) : GameMap()