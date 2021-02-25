package scene.world

import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.plus
import domain.*
import domain.world.PlayerFleet
import domain.world.WorldMap
import ui.LiveData

class WorldViewModel(
    private val store: GameStore
) {
    // 가시범위.
    val viewScale: LiveData<Double> = LiveData(2.0)
    val playerFleet: LiveData<PlayerFleet> = LiveData(null)
    val windDirection: LiveData<Angle> = LiveData(null)
    val windSpeed: LiveData<Double> = LiveData(null)
    val nearPort: LiveData<String> = LiveData(null)
    val nearLanding: LiveData<String> = LiveData(null)

    private fun onMoveFleet(fleet: PlayerFleet) {
        playerFleet(fleet)
        val worldMap = fleet.map
        val txy = fleet.location.toTileXY()
        scanNearPort(txy, worldMap.portPositions)
        scanNearLanding(txy, worldMap.landingPositions)
        // FIXME 바람 데이터는 어디서?
        windDirection(Angle.fromDegrees(0))
        windSpeed(1.0)
        // save current location.
        store.fleet.location = fleet.location
    }

    private fun scanNearPort(tileXY: TileXY, portPositions: Map<TileXY, Port?>) {
        // 항구가 있으면 입항 표시.
        val portIds = tileXY.crossXY.mapNotNull { txy -> portPositions[txy]?.id }
        val portIdString = portIds.firstOrNull()?.name ?: ""
        nearPort(portIdString)
    }

    private fun scanNearLanding(tileXY: TileXY, landingPositions: Map<TileXY, LandingId?>) {
        // 상륙지가 있으면 표시.
        val landingIds = tileXY.crossXY.mapNotNull { txy -> landingPositions[txy] }
        val landingIdString = landingIds.firstOrNull()?.name ?: ""
        nearLanding(landingIdString)
    }

    fun initPlayerFleet(gameMap: WorldMap) {
        val fleet = PlayerFleet(location = store.fleet.location, map = gameMap)
        playerFleet(fleet)
        onMoveFleet(fleet)
    }

    fun enterPort() {
        store.fleet.port = PortId.valueOf(nearPort.get())
    }

    fun enterLanding() {
        store.fleet.landing = LandingId.valueOf(nearLanding.get())
    }

    fun turnLeft() {
        playerFleet.value?.let {
            // FIXME 조타 능력에 따라 각도가 바뀌어야 한다. 속도에도 괜계가 있나?
            it.angle = it.angle.plus(Angle.Companion.fromDegrees(2))
            onMoveFleet(it)
        }
    }

    fun turnRight() {
        playerFleet.value?.let {
            it.angle = it.angle.plus(Angle.Companion.fromDegrees(-2))
            onMoveFleet(it)
        }
    }

    fun move() {
        playerFleet.value?.let {
            it.move(windDirection.value ?: Angle.ZERO, windSpeed.value ?: 0.0)
            onMoveFleet(it)
        }
    }

    fun stop() {
        playerFleet.value?.let {
            it.stop()
            onMoveFleet(it)
        }
    }

    fun controlSail() {
        playerFleet.value?.let {
            it.controlSail()
            onMoveFleet(it)
        }
    }
}