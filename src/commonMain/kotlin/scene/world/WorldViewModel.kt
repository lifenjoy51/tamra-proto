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
    val playerFleet: LiveData<PlayerFleet> = LiveData(null)
    val windDirection: LiveData<Angle> = LiveData(null)
    val windSpeed: LiveData<Double> = LiveData(null)
    val nearPort: LiveData<String> = LiveData(null)
    val nearLanding: LiveData<String> = LiveData(null)

    private fun onMoveFleet(fleet: PlayerFleet) {
        playerFleet(fleet)
        val worldMap = fleet.map
        val txy = fleet.point.toTXY()
        scanNearPort(txy, worldMap.portPositions)
        scanNearLanding(txy, worldMap.landingPositions)
        // FIXME 바람 데이터는 어디서?
        windDirection(Angle.ZERO)
        windSpeed(1.0)
        // save current location.
        store.fleet.location = fleet.point
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
        val fleet = PlayerFleet(point = store.fleet.location, map = gameMap)
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