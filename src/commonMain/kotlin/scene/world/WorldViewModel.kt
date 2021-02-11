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
        val gameMap = fleet.map
        val txy = fleet.point.toTXY(gameMap.tileSize)
        scanNearPort(txy, gameMap.portPositions)
        scanNearLanding(txy, gameMap.landingPositions)
        // FIXME 바람 데이터는 어디서?
        windDirection(Angle.ZERO)
        windSpeed(1.0)
        store.fleet.location = fleet.point
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

    fun speedDown() {
        playerFleet.value?.let {
            it.v = it.v * 0.95
            onMoveFleet(it)
        }
    }

    fun speedUp() {
        playerFleet.value?.let {
            it.v = it.v * 1.05
            onMoveFleet(it)
        }
    }

    fun turnLeft() {
        playerFleet.value?.let {
            it.angle = it.angle.plus(Angle.Companion.fromDegrees(10))
            onMoveFleet(it)
        }
    }

    fun turnRight() {
        playerFleet.value?.let {
            it.angle = it.angle.plus(Angle.Companion.fromDegrees(-10))
            onMoveFleet(it)
        }
    }

    fun move() {
        playerFleet.value?.let {
            it.move(windDirection.value ?: Angle.ZERO, windSpeed.value ?: 0.0)
            onMoveFleet(it)
        }
    }
}