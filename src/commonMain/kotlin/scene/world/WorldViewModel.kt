package scene.world

import domain.*
import domain.world.PlayerFleet
import domain.world.WorldMap
import ui.LiveData

class WorldViewModel(
    private val store: GameStore
) {
    val playerFleet: LiveData<PlayerFleet> = LiveData(null)
    val nearPort: LiveData<String> = LiveData(null)
    val nearLanding: LiveData<String> = LiveData(null)

    private fun onMoveFleet(fleet: PlayerFleet) {
        playerFleet(fleet)
        val gameMap = fleet.map
        val txy = fleet.xy.toTXY(gameMap.tileSize)
        scanNearPort(txy, gameMap.portPositions)
        scanNearLanding(txy, gameMap.landingPositions)
        store.fleet.location = fleet.xy
    }

    private fun scanNearPort(txy: TXY, portPositions: Map<TXY, Port?>) {
        // 항구가 있으면 입항 표시.
        val portIds = txy.crossXY.mapNotNull { txy -> portPositions[txy]?.id }
        val portIdString = portIds.firstOrNull()?.name ?: ""
        nearPort(portIdString)
    }

    private fun scanNearLanding(txy: TXY, landingPositions: Map<TXY, LandingId?>) {
        // 상륙지가 있으면 표시.
        val landingIds = txy.crossXY.mapNotNull { txy -> landingPositions[txy] }
        val landingIdString = landingIds.firstOrNull()?.name ?: ""
        nearLanding(landingIdString)
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
        store.fleet.port = PortId.valueOf(nearPort.get())
    }

    fun enterLanding() {
        store.fleet.landing = LandingId.valueOf(nearLanding.get())
    }
}