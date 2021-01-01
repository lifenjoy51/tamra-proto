package scene.port.shipyard

import ViewModelProvider
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.solidRect
import domain.GameStore
import mainHeight
import mainWidth

class ShipyardScene(private val store: GameStore, viewModelProvider: ViewModelProvider) : Scene() {

    private val vm = viewModelProvider.headerViewModel

    override suspend fun Container.sceneInit() {
        val background = solidRect(width = mainWidth, height = mainHeight)
    }
}