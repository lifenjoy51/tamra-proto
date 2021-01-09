package domain

// FIXME 컨텍스트의 정체를 명확히 해야한다.
data class GameStore(
    val fleet: Fleet
) {
    var playerLocation: XY? = null
    fun port(): Port? = fleet.port?.let { GameData.ports[it] }

}