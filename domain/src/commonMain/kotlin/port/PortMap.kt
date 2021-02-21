package domain.port

import domain.BuildingType
import domain.TileXY

class PortMap(
    val buildingMap: Map<TileXY, BuildingType>
)