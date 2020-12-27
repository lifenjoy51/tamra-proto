package domain.world

import domain.GameUnit
import domain.XY

data class PlayerFleet(
    override var xy: XY,
    override var v: Double = 1.6,
    override var size: XY = XY(8.0, 8.0),
    override val map: WorldMap
) : GameUnit()