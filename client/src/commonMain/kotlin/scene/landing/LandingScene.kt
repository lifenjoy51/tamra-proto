package scene.landing

import com.soywiz.korev.Key
import com.soywiz.korge.input.keys
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.view.Container
import com.soywiz.korio.file.std.resourcesVfs
import scene.event.EventView
import scene.world.WorldScene
import tamra.ViewModelProvider
import tamra.common.SiteId
import tamra.landing.LandingMap
import util.getCollisions
import util.getObjectNames
import util.getTiles

class LandingScene(val viewModelProvider: ViewModelProvider) : Scene() {

    private val landingView = LandingView(
        viewModelProvider,
        { sceneContainer.changeTo<WorldScene>() }
    )
    private val vm = viewModelProvider.landingViewModel

    private val eventView = EventView(viewModelProvider)
    private val eventVm = viewModelProvider.eventViewModel

    override suspend fun Container.sceneInit() {
        // load tiledMap
        val tiledMap = resourcesVfs["landing.tmx"].readTiledMap()
        val sites = tiledMap.getObjectNames("sites")
            .mapValues { SiteId.valueOf(it.value) }

        val tiles = tiledMap.getTiles()
        val collisions = tiledMap.getCollisions()
        val landingMap = LandingMap(sites, tiles, collisions)

        // save
        vm.save()

        // draw ui
        landingView.draw(this, tiledMap)


        // init vm
        vm.init(landingMap)

        // init eventView
        // FIXME eventView.draw(this)

        // input
        keys.down {
            when (it.key) {
                Key.RIGHT -> vm.right()
                Key.LEFT -> vm.left()
                Key.UP -> vm.up()
                Key.DOWN -> vm.down()
            }
        }
    }
}