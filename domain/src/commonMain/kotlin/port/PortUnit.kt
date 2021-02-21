package domain.port

import domain.GameUnit
import domain.LocationXY

data class Player(
    override var point: LocationXY,
    override var v: Double = 1.2,
    override var size: LocationXY = LocationXY(8.0, 8.0),
    val map: PortMap,
) : GameUnit()