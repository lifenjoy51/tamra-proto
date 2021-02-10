package domain.world

import com.soywiz.korma.geom.Point
import domain.GameMap
import domain.LandingId
import domain.Port
import domain.TXY

class WorldMap(
    override val movableArea: List<List<Pair<Point, Point>>>,
    val tileSize: Int,
    val portPositions: Map<TXY, Port?>,
    val landingPositions: Map<TXY, LandingId?>
) : GameMap()