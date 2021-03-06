package scene.world

import com.soywiz.klock.TimeSpan
import com.soywiz.korau.sound.PlaybackTimes
import com.soywiz.korau.sound.SoundChannel
import com.soywiz.korau.sound.readSound
import com.soywiz.korev.Key
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.addFixedUpdater
import com.soywiz.korio.file.std.resourcesVfs
import scene.landing.LandingScene
import scene.port.PortScene
import tamra.ViewModelProvider

class WorldScene(viewModelProvider: ViewModelProvider) : Scene() {

    private lateinit var bgSoundChannel: SoundChannel

    private val worldView = WorldView(
        viewModelProvider,
        { sceneContainer.changeTo<PortScene>() },
        { sceneContainer.changeTo<LandingScene>() }
    )
    private val vm = viewModelProvider.worldViewModel

    override suspend fun sceneBeforeLeaving() {
        super.sceneBeforeLeaving()
        bgSoundChannel.pause()
    }

    override suspend fun Container.sceneInit() {
        // draw ui..
        worldView.draw(this)

        // update
        addFixedUpdater(TimeSpan(100.0)) {
            onKeyInput()
            vm.move()
        }

        // play bg  sound
        val bg = resourcesVfs["tamra.mp3"].readSound()
        bgSoundChannel = bg.play(PlaybackTimes.INFINITE)
    }

    private fun onKeyInput() {
        when {
            views.input.keys[Key.RIGHT] -> vm.turnRight()
            views.input.keys[Key.LEFT] -> vm.turnLeft()
            views.input.keys[Key.UP] -> vm.controlSail()
            views.input.keys[Key.DOWN] -> vm.stop()
        }
    }
}