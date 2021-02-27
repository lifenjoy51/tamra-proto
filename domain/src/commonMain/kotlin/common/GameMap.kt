package tamra.common

abstract class GameMap {
    abstract val tiles: Map<TileXY, TileId>
    abstract val collisions: Map<TileId, List<PointArea>>
    abstract val onWater: Boolean

    abstract fun getTileXY(locationXY: LocationXY): TileXY
}