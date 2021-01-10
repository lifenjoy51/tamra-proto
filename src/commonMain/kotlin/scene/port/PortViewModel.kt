package scene.port

import com.soywiz.korio.file.std.resourcesVfs
import domain.BuildingType
import domain.GameStore
import domain.TXY
import domain.XY
import domain.port.Player
import domain.port.PortMap
import ui.LiveData
import util.SaveManager

class PortViewModel(
    private val store: GameStore
) {
    val player: LiveData<Player> = LiveData(null)
    val currentBuilding: LiveData<String> = LiveData(null)

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

    suspend fun save() {
        // save
        SaveManager.save(store).let {
            resourcesVfs["saved.json"].writeString(it)
        }
    }

    fun init(portMap: PortMap) {
        initPlayer(portMap)
    }

    private fun initPlayer(portMap: PortMap) {
        val playerXy: XY = store.playerLocation?.let { it } ?: run {
            val dockTxy = portMap.buildingMap.filterValues { it == BuildingType.DOCK }.keys.first()
            val startXy = dockTxy.toXY(portMap.tileSize)
            startXy.copy(
                x = startXy.x + portMap.tileSize / 2,
                y = startXy.y + portMap.tileSize / 2
            )
        }
        val p = Player(xy = playerXy, map = portMap)
        player(p)
        onMovePlayer(p)

    }

    private fun onMovePlayer(p: Player) {
        player(p)
        val gameMap = p.map
        val txy = p.xy.toTXY(gameMap.tileSize)
        scanBuilding(txy, gameMap.buildingMap)
        store.playerLocation = p.xy
    }

    private fun scanBuilding(txy: TXY, buildingMap: Map<TXY, BuildingType>) {
        val buildingName = buildingMap[txy]?.let { it.name } ?: ""
        currentBuilding(buildingName)
    }

    fun leavePort() {
        store.fleet.port = null
    }
}