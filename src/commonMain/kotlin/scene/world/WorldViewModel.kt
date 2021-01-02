package scene.world

import domain.GameStore
import domain.Port
import domain.PortId
import domain.TXY
import domain.world.PlayerFleet
import domain.world.WorldMap
import ui.LiveData

class WorldViewModel(
    private val store: GameStore
) {
    val playerFleet: LiveData<PlayerFleet> = LiveData(null)
    val nearPort: LiveData<String> = LiveData(null)
    val toggleFleetInfo: LiveData<Boolean> = LiveData(false)
    val money: LiveData<Int> = LiveData(null)

    private fun onMoveFleet(fleet: PlayerFleet) {
        playerFleet(fleet)
        val gameMap = fleet.map
        val txy = fleet.xy.toTXY(gameMap.tileSize)
        scanNearPort(txy, gameMap.portPositions)
        store.fleet.location = fleet.xy
    }

    private fun scanNearPort(txy: TXY, portPositions: Map<TXY, Port?>) {
        // 항구가 있으면 입항 표시.
        val portIds = txy.crossXY.mapNotNull { txy -> portPositions[txy]?.id }
        val portIdString = portIds.firstOrNull()?.name ?: ""
        nearPort(portIdString)
    }

    fun up() {
        playerFleet.value?.let {
            it.moveUp()
            onMoveFleet(it)
        }
    }

    fun down() {
        playerFleet.value?.let {
            it.moveDown()
            onMoveFleet(it)
        }
    }

    fun left() {
        playerFleet.value?.let {
            it.moveLeft()
            onMoveFleet(it)
        }
    }

    fun right() {
        playerFleet.value?.let {
            it.moveRight()
            onMoveFleet(it)
        }
    }

    fun initPlayerFleet(gameMap: WorldMap) {
        val fleet = PlayerFleet(xy = store.fleet.location, map = gameMap)
        playerFleet(fleet)
        onMoveFleet(fleet)
    }

    fun enterPort() {
        store.fleet.port = PortId.valueOf(nearPort.value!!)
    }

    fun initMoney() {
        money(store.fleet.balance)
    }
}