package domain.port

import com.soywiz.korma.geom.Point
import domain.BuildingType
import domain.GameMap
import domain.TXY

class PortMap(
    override val movableArea: List<List<Pair<Point, Point>>>,
    val tileSize: Int,
    val buildingMap: Map<TXY, BuildingType>
) : GameMap()