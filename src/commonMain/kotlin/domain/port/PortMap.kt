package domain.port

import domain.BuildingType
import domain.GameMap
import domain.TXY
import domain.XY

class PortMap(
    override val movableArea: List<List<Pair<XY, XY>>>,
    val tileSize: Int,
    val buildingMap: Map<TXY, BuildingType>
) : GameMap()