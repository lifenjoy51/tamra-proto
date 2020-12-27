package domain

// FIXME 컨텍스트의 정체를 명확히 해야한다.
data class GameStore(
    val ships: MutableList<Ship>,   // 플레이어의 배 목록
    val money: Int,
    var port: PortId?,  // 현재 정박중인 항구.
    var location: XY    // 현재 위치.
)