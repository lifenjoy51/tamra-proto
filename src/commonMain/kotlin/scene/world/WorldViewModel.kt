package scene.world

import domain.Port
import domain.TXY
import domain.world.PlayerFleet
import ui.LiveData

class WorldViewModel internal constructor(
        val fleet: LiveData<PlayerFleet> = LiveData(null),
        val port: LiveData<String> = LiveData(null),
        val toggleEnterPort: LiveData<Boolean> = LiveData(false),
        val toggleFleetInfo: LiveData<Boolean> = LiveData(false)
) {
    companion object {
        val instance = WorldViewModel()
    }

    fun onMoveFleet(playerFleet: PlayerFleet) {
        fleet(playerFleet, true)
        val gameMap = playerFleet.map
        val txy = playerFleet.xy.toTXY(gameMap.tileSize)
        scanNearPort(txy, gameMap.portPositions)
    }


    private fun scanNearPort(txy: TXY, portPositions: Map<TXY, Port?>) {
        // 항구가 있으면 입항 표시.
        val portIds = txy.crossXY.mapNotNull { txy -> portPositions[txy]?.id }
        val portIdString = portIds.firstOrNull()?.name ?: ""
        port(portIdString)
        toggleEnterPort(portIdString.isNotEmpty())
    }

    fun up() {
        fleet.value?.let {
            it.moveUp()
            onMoveFleet(it)
        }
    }

    fun down() {
        fleet.value?.let {
            it.moveDown()
            onMoveFleet(it)
        }
    }

    fun left() {
        fleet.value?.let {
            it.moveLeft()
            onMoveFleet(it)
        }
    }

    fun right() {
        fleet.value?.let {
            it.moveRight()
            onMoveFleet(it)
        }
    }
}