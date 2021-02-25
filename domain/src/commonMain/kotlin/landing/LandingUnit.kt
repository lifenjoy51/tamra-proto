package domain.landing

import domain.GameUnit
import domain.LocationXY

data class LandingPlayer(
    override var location: LocationXY,
    override var velocity: Double = 1.2,
    val map: LandingMap,
) : GameUnit()