package domain.world

import domain.LandingId
import domain.LocationXY
import domain.Port
import domain.TileXY

class WorldMap(
    val portPositions: Map<TileXY, Port?>,
    val landingPositions: Map<TileXY, LandingId?>,
    val tiles: Map<TileXY, Int>,
    val tileCollisions: Map<Int, List<List<Pair<LocationXY, LocationXY>>>>
)