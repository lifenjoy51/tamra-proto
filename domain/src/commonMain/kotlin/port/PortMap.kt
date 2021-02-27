package tamra.port

import tamra.common.*

class PortMap(
    val buildingMap: Map<TileXY, BuildingType>,
    override val tiles: Map<TileXY, TileId>,
    override val collisions: Map<TileId, List<PointArea>>
) : GameMap() {
    override val onWater: Boolean = false
    override fun getTileXY(locationXY: LocationXY): TileXY {
        return locationXY.toTileXY()
    }
}