package domain.port

import domain.GameMap
import domain.TXY
import domain.XY

class PortMap(
        override val movableArea: List<List<Pair<XY, XY>>>,
        val tileSize: Int,
        val buildingPositions: Map<TXY, String?>
) : GameMap()