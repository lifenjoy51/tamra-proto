package tamra.landing

import tamra.common.*

class LandingMap(
    val sites: Map<TileXY, SiteId>,
    override val tiles: Map<TileXY, TileId>,
    override val collisions: Map<TileId, List<PointArea>>
) : GameMap() {
    override val onWater: Boolean = false
    override fun getTileXY(locationXY: LocationXY): TileXY {
        return locationXY.toTileXY()
    }
}