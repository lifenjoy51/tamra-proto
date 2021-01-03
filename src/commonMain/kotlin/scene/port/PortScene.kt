package scene.port

import ViewModelProvider
import com.soywiz.klock.TimeSpan
import com.soywiz.korev.Key
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.addFixedUpdater
import com.soywiz.korio.file.std.resourcesVfs
import domain.BuildingType
import domain.port.PortMap
import scene.port.market.MarketScene
import scene.port.shipyard.ShipyardScene
import scene.world.WorldScene
import util.getMovableArea
import util.getObjectNames

class PortScene(viewModelProvider: ViewModelProvider) : Scene() {

    private val portView = PortView(
        viewModelProvider,
        { sceneContainer.changeTo<WorldScene>() },
        { sceneContainer.changeTo<MarketScene>() },
        { sceneContainer.changeTo<ShipyardScene>() },
    )
    private val vm = viewModelProvider.portViewModel

    override suspend fun Container.sceneInit() {
        // load tiledMap
        val tiledMap = resourcesVfs["port.tmx"].readTiledMap()
        val movableArea = tiledMap.getMovableArea()
        val buildings = tiledMap.getObjectNames("buildings").mapValues {
            BuildingType.valueOf(it.value)
        }

        val portMap = PortMap(movableArea, tiledMap.tileheight, buildings)

        // save
        vm.save()

        // draw ui
        portView.draw(this, tiledMap)

        // init vm
        vm.initPlayer(portMap)

        // update
        addFixedUpdater(TimeSpan(100.0)) {
            onKeyInput()
        }
    }

    private fun onKeyInput() {
        when {
            views.input.keys[Key.RIGHT] -> vm.right()
            views.input.keys[Key.LEFT] -> vm.left()
            views.input.keys[Key.UP] -> vm.up()
            views.input.keys[Key.DOWN] -> vm.down()
        }
    }
}