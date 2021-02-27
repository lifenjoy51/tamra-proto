package tamra.landing

import tamra.common.GameUnit
import tamra.common.LocationXY

data class LandingPlayer(
    override var location: LocationXY,
    override var velocity: Double = 1.2,
    override val map: LandingMap,
) : GameUnit()