package scene.port

import domain.*
import domain.port.Player
import domain.port.PortMap
import ui.LiveData

class PortViewModel(
    private val store: GameStore
) {
    val player: LiveData<Player> = LiveData(null)
    val currentBuilding: LiveData<String> = LiveData(null)
    val port: LiveData<Port> = LiveData(null)

    fun up() {
        player.value?.let {
            it.moveUp()
            onMovePlayer(it)
        }
    }

    fun down() {
        player.value?.let {
            it.moveDown()
            onMovePlayer(it)
        }
    }

    fun left() {
        player.value?.let {
            it.moveLeft()
            onMovePlayer(it)
        }
    }

    fun right() {
        player.value?.let {
            it.moveRight()
            onMovePlayer(it)
        }
    }

    fun init() {
        port(GameData.ports[store.port]!!)
    }

    fun initPlayer(portMap: PortMap) {
        val dockTxy = portMap.buildingMap.filterValues { it == BuildingType.DOCK }.keys.first()
        val startXy = dockTxy.toXY(portMap.tileSize)
        val p = Player(xy = startXy.copy(
            x = startXy.x + portMap.tileSize / 2,
            y = startXy.y + portMap.tileSize / 2
        ), map = portMap)
        player(p)
        onMovePlayer(p)
    }

    private fun onMovePlayer(p: Player) {
        player(p)
        val gameMap = p.map
        val txy = p.xy.toTXY(gameMap.tileSize)
        scanBuilding(txy, gameMap.buildingMap)
    }

    private fun scanBuilding(txy: TXY, buildingMap: Map<TXY, BuildingType>) {
        val buildingName = buildingMap[txy]?.let { it.name } ?: ""
        currentBuilding(buildingName)
    }

    fun leavePort() {
        store.port = null
    }
}