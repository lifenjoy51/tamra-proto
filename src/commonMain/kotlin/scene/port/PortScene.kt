package scene.port

import ViewModelProvider
import com.soywiz.klock.TimeSpan
import com.soywiz.korev.Key
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.addFixedUpdater
import com.soywiz.korio.file.std.resourcesVfs
import domain.GameStore
import scene.world.WorldScene
import util.SaveManager

class PortScene(private val store: GameStore, viewModelProvider: ViewModelProvider) : Scene() {

    private val portView = PortView(
        viewModelProvider
    ) { sceneContainer.changeTo<WorldScene>() }
    private val vm = viewModelProvider.portViewModel

    override suspend fun Container.sceneInit() {
        // save
        SaveManager.save(store).let {
            resourcesVfs["saved.json"].writeString(it)
        }

        // draw ui
        portView.draw(this)

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