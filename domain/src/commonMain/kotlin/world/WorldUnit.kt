package tamra.world

import com.soywiz.korma.geom.*
import tamra.common.GameUnit
import tamra.common.LocationXY

data class PlayerFleet(
    override var location: LocationXY,
    override var velocity: Double = 0.1,
    override val map: WorldMap,
    var angle: Angle = Angle.fromDegrees(90),
    var sailState: SaleState = SaleState.CLOSE_SALE,
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
        val originVelocity = velocity
        velocity = when (sailState) {
            SaleState.FULL_SALE -> velocity + (((a - 0.5) * 3 * windSpeed - 0.03) / 50)
            SaleState.CLOSE_SALE -> velocity + (((a * 0.3) * windSpeed - 0.03) / 50)
            SaleState.STOP -> velocity * 0.5
        }

        // FIXME 최대속도 제한.
        if (velocity > 1) velocity = 1.0
        if (velocity < -1) velocity = originVelocity

        // FIXME test
        // velocity = 0.5

        // 0도 기준으로 위아래로 움직이는게 cosine, 좌우로 움직이는게 sine이다.
        val dx = -angle.sine * velocity
        val dy = -angle.cosine * velocity
        if (!this.moved(dx, dy) || originVelocity == 0.0) {
            velocity = originVelocity * 0.5
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
