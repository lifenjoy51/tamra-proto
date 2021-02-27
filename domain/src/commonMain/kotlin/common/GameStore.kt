package tamra.common

// FIXME 컨텍스트의 정체를 명확히 해야한다.
data class GameStore(
    val fleet: Fleet,
    val doneEvents: MutableList<String> = mutableListOf(),  // 완료한 이벤트 아이디. TODO 완료시 추가.
) {
    var playerLocation: LocationXY? = null
    fun port(): Port? = fleet.port?.let { GameData.ports[it] }

}