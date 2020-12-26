package scene.world

import com.soywiz.klock.TimeSpan
import com.soywiz.korev.Key
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.addFixedUpdater
import domain.GameContext


class WorldScene(private val context: GameContext) : Scene() {

    private val worldView = WorldView(context, WorldViewModel.instance, this)
    private val vm = WorldViewModel.instance

    override suspend fun Container.sceneInit() {
        // draw ui..
        worldView.draw(this)

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