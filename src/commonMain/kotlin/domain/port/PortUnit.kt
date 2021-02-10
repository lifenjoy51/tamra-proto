package domain.port

import com.soywiz.korma.geom.Point
import domain.GameUnit

data class Player(
    override var point: Point,
    override var v: Double = 1.2,
    override var size: Point = Point(8.0, 8.0),
    override val map: PortMap,
) : GameUnit()