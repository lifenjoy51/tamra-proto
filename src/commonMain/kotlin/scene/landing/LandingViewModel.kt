package scene.landing

import com.soywiz.korio.file.std.resourcesVfs
import domain.GameStore
import domain.SiteId
import domain.TXY
import domain.XY
import domain.landing.LandingMap
import domain.landing.LandingPlayer
import ui.LiveData
import util.SaveManager

class LandingViewModel(
    private val store: GameStore
) {
    val player: LiveData<LandingPlayer> = LiveData(null)
    val currentSite: LiveData<String> = LiveData(null)

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

    fun init(landingMap: LandingMap) {
        initPlayer(landingMap)
    }

    private fun initPlayer(landingMap: LandingMap) {
        val playerXy: XY = store.playerLocation?.let { it } ?: run {
            val exitTxy = landingMap.sites.filterValues { it == SiteId.EXIT }.keys.first()
            val startXy = exitTxy.toXY(landingMap.tileSize)
            startXy.copy(
                x = startXy.x + landingMap.tileSize / 2,
                y = startXy.y + landingMap.tileSize / 2
            )
        }
        val p = LandingPlayer(xy = playerXy, map = landingMap)
        player(p)
        onMovePlayer(p)

    }

    private fun onMovePlayer(p: LandingPlayer) {
        player(p)
        val gameMap = p.map
        val txy = p.xy.toTXY(gameMap.tileSize)
        scanBuilding(txy, gameMap.sites)
        store.playerLocation = p.xy
    }

    private fun scanBuilding(txy: TXY, buildingMap: Map<TXY, SiteId>) {
        val buildingName = buildingMap[txy]?.let { it.name } ?: ""
        currentSite(buildingName)
    }

    fun leaveLanding() {
        store.fleet.landing = null
    }
}