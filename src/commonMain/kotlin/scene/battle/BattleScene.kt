package scene.battle

import com.soywiz.korge.input.keys
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.view.Container
import com.soywiz.korio.file.std.resourcesVfs
import scene.event.EventView
import scene.world.WorldScene
import tamra.ViewModelProvider
import tamra.battle.BattleMap
import tamra.common.BattleSiteId
import util.getCollisions
import util.getObjectNames
import util.getTiles

class BattleScene(val viewModelProvider: ViewModelProvider) : Scene() {

    private val battleView = BattleView(
        viewModelProvider,
        { sceneContainer.changeTo<WorldScene>() }
    )
    private val vm = viewModelProvider.battleViewModel

    private val eventView = EventView(viewModelProvider)
    private val eventVm = viewModelProvider.eventViewModel

    override suspend fun Container.sceneInit() {
        // load tiledMap
        val tiledMap = resourcesVfs["battle.tmx"].readTiledMap()
        val sites = tiledMap.getObjectNames("sites")
            .mapValues { BattleSiteId.valueOf(it.value) }
            .entries
            .associate { it.value to it.key }

        val tiles = tiledMap.getTiles()
        val collisions = tiledMap.getCollisions()
        val battleMap = BattleMap(sites, tiledMap.width, tiledMap.height, tiles, collisions)

        // draw ui
        battleView.draw(this, tiledMap)


        // init vm
        vm.init(battleMap)

        // init eventView
        // FIXME eventView.draw(this)

        // input
        keys.down {
            when (it.key) {
            }
        }
    }
}