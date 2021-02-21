package domain.landing

import domain.GameUnit
import domain.LocationXY

data class LandingPlayer(
    override var point: LocationXY,
    override var v: Double = 1.2,
    override var size: LocationXY = LocationXY(8.0, 8.0),
    val map: LandingMap,
) : GameUnit()