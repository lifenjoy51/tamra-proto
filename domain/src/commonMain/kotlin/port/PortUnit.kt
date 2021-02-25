package domain.port

import domain.GameUnit
import domain.LocationXY

data class Player(
    override var location: LocationXY,
    override var velocity: Double = 1.2,
    val map: PortMap,
) : GameUnit()