package scene.port

import com.soywiz.korev.Key
import com.soywiz.korge.input.keys
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.view.Container
import com.soywiz.korio.file.std.resourcesVfs
import scene.event.EventView
import scene.port.market.MarketScene
import scene.port.shipyard.ShipyardScene
import scene.world.WorldScene
import tamra.ViewModelProvider
import tamra.common.BuildingType
import tamra.port.PortMap
import util.getCollisions
import util.getObjectNames
import util.getTiles

class PortScene(val viewModelProvider: ViewModelProvider) : Scene() {

    private val portView = PortView(
        viewModelProvider,
        { sceneContainer.changeTo<WorldScene>() },
        { sceneContainer.changeTo<MarketScene>() },
        { sceneContainer.changeTo<ShipyardScene>() },
    )
    private val vm = viewModelProvider.portViewModel

    private val eventView = EventView(viewModelProvider)
    private val eventVm = viewModelProvider.eventViewModel

    override suspend fun Container.sceneInit() {
        // load tiledMap
        val tiledMap = resourcesVfs["port.tmx"].readTiledMap()
        val buildings = tiledMap.getObjectNames("buildings")
            .mapValues { BuildingType.valueOf(it.value) }

        val tiles = tiledMap.getTiles()
        val collisions = tiledMap.getCollisions()
        val portMap = PortMap(buildings, tiles, collisions)

        // save
        vm.save()

        // draw ui
        portView.draw(this, tiledMap)

        // init vm
        vm.init(portMap)

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