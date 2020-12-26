package domain.world

import domain.GameMap
import domain.Port
import domain.TXY
import domain.XY

class WorldMap(
        override val movableArea: List<List<Pair<XY, XY>>>,
        val tileSize: Int,
        val portPositions: Map<TXY, Port?>
) : GameMap()