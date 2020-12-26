package scene

import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onOut
import com.soywiz.korge.input.onOver
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import domain.GameContext
import mainHeight
import mainWidth
import scene.world.WorldScene
import ui.tamraText

class MainScene(val gameContext: GameContext) : Scene() {
    override suspend fun Container.sceneInit() {
        // background
        solidRect(mainWidth, mainHeight, Colors.LIGHTBLUE) {

        }
        // title
        tamraText("탐라", color = Colors.DARKBLUE, textSize = 96.0) {
            centerXOnStage()
            positionY(64)
        }
        // logo
        sprite(
                texture = resourcesVfs["L200.png"].readBitmap()
        ) {
            centerOnStage()
        }
        // start
        tamraText("Touch to Start", textSize = 36.0, color = Colors.DARKBLUE) {
            centerXOnStage()
            positionY(mainHeight * 2 / 3)
            alpha = 0.8
            onOut { alpha = 0.8 }
            onOver { alpha = 1.0 }
            onClick {
                sceneContainer.changeTo<WorldScene>()
            }
        }

    }
}