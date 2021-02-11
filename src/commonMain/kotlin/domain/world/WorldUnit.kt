package domain.world

import com.soywiz.korma.geom.*
import domain.GameUnit

data class PlayerFleet(
    override var point: Point,
    override var v: Double = 0.1,
    override var size: Point = Point(8.0, 8.0),
    override val map: WorldMap,
    var angle: Angle = Angle.ZERO
// 정박/반개/전개
// 풍향?
// 돛 각도.

) : GameUnit() {

    // 외부적인 요인은 받아야 한다.
    // 풍향, 풍속, 가장 중요하다.
    // 해류, 속도. 이것도 후순위로 미룰 수 있다.
    // 파도는 특수 이벤트 처리...
    fun move(windDirection: Angle, windSpeed: Double) {
        // 가속도. 풍향등을 고려해서 계산.
        // 현재 각도와 일치하면 최고 속력.
        // 현재 각도와 정 반대이면 마이너스.

        // 두 각도의 차가 0이면 최고 속도.
        // 두 각도의 차가 90이면 낮음.
        // 두 각도의 차가 135이상이면 0.
        // 180이면 마이너스.
        val angleDiff = abs(windDirection.shortDistanceTo(angle))
        val a = ((180 - angleDiff.degrees) / 180) - 0.5
        // 가속도를 속도에 반영한다.
        v += a / 50
        println("$angleDiff $a $v")
        if (v > 5) v = 5.0
        if (v <= 0) v = 0.1
        // 0도 기준으로 위아래로 움직이는게 cosine, 좌우로 움직이는게 sine이다.
        val dx = -angle.sine * v
        val dy = -angle.cosine * v
        move(dx, dy)
    }
}