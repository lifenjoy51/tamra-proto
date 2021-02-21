package domain.port

import com.soywiz.korma.geom.Point
import domain.BuildingType
import domain.GameMap
import domain.TileXY

class PortMap(
    override val movableArea: List<List<Pair<Point, Point>>>,
    val buildingMap: Map<TileXY, BuildingType>
) : GameMap()