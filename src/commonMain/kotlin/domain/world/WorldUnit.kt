package domain.world

import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.cosine
import com.soywiz.korma.geom.sine
import domain.GameUnit

data class PlayerFleet(
    override var point: Point,
    override var v: Double = 0.1,
    override var size: Point = Point(8.0, 8.0),
    override val map: WorldMap,
    var angle: Angle = Angle.ZERO
) : GameUnit() {
    fun move() {
        // 0도 기준으로 위아래로 움직이는게 cosine, 좌우로 움직이는게 sine이다.
        val dx = -angle.sine * v
        val dy = -angle.cosine * v
        move(dx, dy)
    }
}