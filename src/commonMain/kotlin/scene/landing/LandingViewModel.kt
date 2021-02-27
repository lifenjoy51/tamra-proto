package scene.landing

import com.soywiz.korio.file.std.resourcesVfs
import tamra.common.*
import tamra.landing.LandingMap
import tamra.landing.LandingPlayer
import ui.LiveData
import util.SaveManager

class LandingViewModel(
    private val store: GameStore
) {
    val player: LiveData<LandingPlayer> = LiveData(null)
    val playerDirection: LiveData<Direction> = LiveData(Direction.DOWN)
    val currentSite: LiveData<String> = LiveData(null)

    fun down() {
        player.value?.let {
            it.moveDown()
            onMovePlayer(it)
            playerDirection(Direction.DOWN)
        }
    }

    fun up() {
        player.value?.let {
            it.moveUp()
            onMovePlayer(it)
            playerDirection(Direction.UP)
        }
    }

    fun right() {
        player.value?.let {
            it.moveRight()
            onMovePlayer(it)
            playerDirection(Direction.RIGHT)
        }
    }

    fun left() {
        player.value?.let {
            it.moveLeft()
            onMovePlayer(it)
            playerDirection(Direction.LEFT)
        }
    }

    suspend fun save() {
        // save
        SaveManager.save(store).let {
            resourcesVfs["saved.json"].writeString(it)
        }
    }

    fun init(landingMap: LandingMap) {
        initPlayer(landingMap)
    }

    private fun initPlayer(landingMap: LandingMap) {
        val playerPoint = store.playerLocation?.let { it } ?: run {
            val exitTxy = landingMap.sites.filterValues { it == SiteId.EXIT }.keys.first()
            val startXy = exitTxy.toLocationXY()
            startXy.copy(
                x = startXy.x + tileSize / 2,
                y = startXy.y + tileSize / 2
            )
        }
        val p = LandingPlayer(location = playerPoint, map = landingMap)
        player(p)
        onMovePlayer(p)

    }

    private fun onMovePlayer(p: LandingPlayer) {
        player(p)
        val gameMap = p.map
        val txy = p.location.toWorldTileXY()
        scanBuilding(txy, gameMap.sites)
        store.playerLocation = p.location
    }

    private fun scanBuilding(tileXY: TileXY, buildingMap: Map<TileXY, SiteId>) {
        val buildingName = buildingMap[tileXY]?.let { it.name } ?: ""
        currentSite(buildingName)
    }

    fun leaveLanding() {
        store.fleet.landing = null
    }
}