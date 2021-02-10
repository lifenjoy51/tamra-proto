package domain.world

import domain.*

class WorldMap(
    override val movableArea: List<List<Pair<XY, XY>>>,
    val tileSize: Int,
    val portPositions: Map<TXY, Port?>,
    val landingPositions: Map<TXY, LandingId?>
) : GameMap()