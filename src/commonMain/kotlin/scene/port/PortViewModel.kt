package scene.port

import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.Point
import domain.BuildingType
import domain.GameStore
import domain.TileXY
import domain.port.Player
import domain.port.PortMap
import domain.toTXY
import tileSize
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
        val playerPoint: Point = store.playerLocation?.let { it } ?: run {
            val dockTxy = portMap.buildingMap.filterValues { it == BuildingType.DOCK }.keys.first()
            val startXy = dockTxy.toXY()
            startXy.copy(
                x = startXy.x + tileSize / 2,
                y = startXy.y + tileSize / 2
            )
        }
        val p = Player(point = playerPoint, map = portMap)
        player(p)
        onMovePlayer(p)

    }

    private fun onMovePlayer(p: Player) {
        player(p)
        val gameMap = p.map
        val txy = p.point.toTXY()
        scanBuilding(txy, gameMap.buildingMap)
        store.playerLocation = p.point
    }

    private fun scanBuilding(tileXY: TileXY, buildingMap: Map<TileXY, BuildingType>) {
        val buildingName = buildingMap[tileXY]?.let { it.name } ?: ""
        currentBuilding(buildingName)
    }

    fun leavePort() {
        store.fleet.port = null
    }
}