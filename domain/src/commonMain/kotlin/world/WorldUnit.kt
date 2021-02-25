package domain.world

import LineHelper
import com.soywiz.korma.geom.*
import domain.GameUnit
import domain.LocationXY
import domain.tileSize
import domain.toTileXY

data class PlayerFleet(
    override var location: LocationXY,
    override var velocity: Double = 0.1,
    val map: WorldMap,
    var angle: Angle = Angle.fromDegrees(90),
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
        //velocity = 0.5

        // 0도 기준으로 위아래로 움직이는게 cosine, 좌우로 움직이는게 sine이다.
        val dx = -angle.sine * velocity
        val dy = -angle.cosine * velocity
        if (!this.moved(dx, dy) || originVelocity == 0.0) {
            velocity = originVelocity * 0.5
        }
    }

    private fun moved(dx: Double, dy: Double): Boolean {
        val newXy = LocationXY(location.x + dx, location.y + dy)
        val moved = isMovable(newXy)
        if (moved) location = newXy
        return moved
    }

    private fun isMovable(location: LocationXY): Boolean {
        val txy = location.toTileXY()
        val collisions = map.tileCollisions[map.tiles[txy]]
        val p1 = location.abs().mod(tileSize)
        val p2 = LocationXY(0.0, location.y).abs().mod(tileSize)
        val base = p1 to p2
        // 교점의 수가 홀수이면 안에 위치.
        // https://en.wikipedia.org/wiki/LocationXY_in_polygon
        return !(collisions?.any { area ->
            area.map {
                if (LineHelper.doIntersect(base, it)) 1 else 0
            }.sum() % 2 == 1
        } ?: true)
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

private fun LocationXY.mod(tileSize: Int): LocationXY {
    val x = this.x % tileSize.toDouble()
    val y = this.y % tileSize.toDouble()
    return LocationXY(x, y)
}
