package tamra.battle

import tamra.common.*

class BattleMap(
    val sites: Map<BattleSiteId, TileXY>,
    val width: Int,
    val height: Int,
    override val tiles: Map<TileXY, TileId>,
    override val collisions: Map<TileId, List<PointArea>>
) : GameMap() {
    override val onWater: Boolean = true
    override fun getTileXY(locationXY: LocationXY): TileXY {
        return locationXY.toTileXY()
    }
}