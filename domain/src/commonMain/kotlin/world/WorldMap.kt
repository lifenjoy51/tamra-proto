package tamra.world

import tamra.common.*

class WorldMap(
    val portPositions: Map<TileXY, PortId?>,
    val landingPositions: Map<TileXY, LandingId?>,
    override val tiles: Map<TileXY, TileId>,
    override val collisions: Map<TileId, List<PointArea>>
) : GameMap() {
    override val onWater: Boolean = true
    override fun getTileXY(locationXY: LocationXY): TileXY {
        return locationXY.toWorldTileXY()
    }
}