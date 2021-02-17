package domain.world

import com.soywiz.korma.geom.*
import domain.GameUnit

data class PlayerFleet(
    override var point: Point,
    override var v: Double = 0.1,
    override var size: Point = Point(8.0, 8.0),
    override val map: WorldMap,
    var angle: Angle = Angle.ZERO,
    var sailState: SaleState = SaleState.CLOSE_SALE
// 정박/반개/전개
// 풍향?
// 돛 각도.

) : GameUnit() {

    enum class SaleState {
        FULL_SALE,
        CLOSE_SALE,
        STOP
    }

    // 외부적인 요인은 받아야 한다.
    // 풍향, 풍속, 가장 중요하다.
    // 해류, 속도. 이것도 후순위로 미룰 수 있다.
    // 파도는 특수 이벤트 처리...
    fun move(windDirection: Angle, windSpeed: Double) {
        //
        val angleDiff = abs(windDirection.shortDistanceTo(angle))
        val a = ((180 - angleDiff.degrees) / 180)

        // 가속도를 속도에 반영한다.
        val originVelocity = v
        v = when (sailState) {
            SaleState.FULL_SALE -> v + (((a - 0.5) * 3 * windSpeed - 0.03) / 30)
            SaleState.CLOSE_SALE -> v + (((a * 0.3) * windSpeed - 0.03) / 30)
            SaleState.STOP -> v * 0.5
        }

        // FIXME 최대속도 제한.
        if (v > 5) v = 5.0
        if (v < -1) v = originVelocity

        // 0도 기준으로 위아래로 움직이는게 cosine, 좌우로 움직이는게 sine이다.
        val dx = -angle.sine * v
        val dy = -angle.cosine * v
        if (!move(dx, dy) || originVelocity == 0.0) {
            v = originVelocity
        }
    }

    fun controlSail() {
        sailState = when (sailState) {
            SaleState.FULL_SALE -> SaleState.CLOSE_SALE
            SaleState.CLOSE_SALE -> SaleState.FULL_SALE
            SaleState.STOP -> SaleState.CLOSE_SALE
        }
    }

    fun stop() {
        sailState = SaleState.STOP
    }
}