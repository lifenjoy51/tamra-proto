package scene

import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onOut
import com.soywiz.korge.input.onOver
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import mainHeight
import mainWidth
import scene.world.WorldScene
import ui.tamraText

class MainScene : Scene() {
    override suspend fun Container.sceneInit() {
        // background
        solidRect(mainWidth, mainHeight, Colors.LIGHTBLUE) {}

        // title
        tamraText("탐라", color = Colors.DARKBLUE, textSize = 96.0, py = 64) {
            centerXOnStage()
        }

        // logo
        sprite(texture = resourcesVfs["L200.png"].readBitmap()) {
            centerOnStage()
        }

        // start
        tamraText("Touch to Start", textSize = 36.0, color = Colors.DARKBLUE, py = mainHeight * 2 / 3) {
            centerXOnStage()
            alpha = 0.8
            onOut { alpha = 0.8 }
            onOver { alpha = 1.0 }
            onClick {
                sceneContainer.changeTo<WorldScene>()
            }
        }
    }
}