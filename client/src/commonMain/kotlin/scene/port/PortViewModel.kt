package scene.port

import com.soywiz.korio.file.std.resourcesVfs
import tamra.common.*
import tamra.port.Player
import tamra.port.PortMap
import ui.LiveData
import util.SaveManager

class PortViewModel(
    private val store: GameStore
) {
    val player: LiveData<Player> = LiveData(null)
    val playerDirection: LiveData<Direction> = LiveData(Direction.DOWN)
    val currentBuilding: LiveData<String> = LiveData(null)

    fun up() {
        player.value?.let {
            it.moveUp()
            onMovePlayer(it)
            playerDirection(Direction.UP)
        }
    }

    fun down() {
        player.value?.let {
            it.moveDown()
            onMovePlayer(it)
            playerDirection(Direction.DOWN)
        }
    }

    fun left() {
        player.value?.let {
            it.moveLeft()
            onMovePlayer(it)
            playerDirection(Direction.LEFT)
        }
    }

    fun right() {
        player.value?.let {
            it.moveRight()
            onMovePlayer(it)
            playerDirection(Direction.RIGHT)
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
        val playerPoint = store.playerLocation?.let { it } ?: run {
            val dockTxy = portMap.buildingMap.filterValues { it == BuildingType.DOCK }.keys.first()
            val startXy = dockTxy.toLocationXY()
            startXy.copy(
                x = startXy.x + tileSize / 2,
                y = startXy.y + tileSize / 2
            )
        }
        val p = Player(location = playerPoint, map = portMap)
        player(p)
        onMovePlayer(p)

    }

    private fun onMovePlayer(p: Player) {
        player(p)
        val gameMap = p.map
        val txy = p.location.toTileXY()
        scanBuilding(txy, gameMap.buildingMap)
        store.playerLocation = p.location
    }

    private fun scanBuilding(tileXY: TileXY, buildingMap: Map<TileXY, BuildingType>) {
        val buildingName = buildingMap[tileXY]?.let { it.name } ?: ""
        currentBuilding(buildingName)
    }

    fun leavePort() {
        store.fleet.port = null
    }
}