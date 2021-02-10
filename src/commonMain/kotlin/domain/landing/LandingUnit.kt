package domain.landing

import domain.GameUnit
import domain.XY

data class LandingPlayer(
    override var xy: XY,
    override var v: Double = 1.2,
    override var size: XY = XY(8.0, 8.0),
    override val map: LandingMap,
) : GameUnit()