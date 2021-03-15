package scene

import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onOut
import com.soywiz.korge.input.onOver
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.lang.substr
import scene.world.WorldScene
import tamra.mainHeight
import tamra.mainWidth
import ui.BACKSPACE
import ui.keyboard
import ui.tamraRect
import ui.tamraText

class MainScene : Scene() {
    override suspend fun Container.sceneInit() {

        // background
        solidRect(mainWidth, mainHeight, Colors.DARKGREY)

        // logo
        val logo = sprite(texture = resourcesVfs["L200.png"].readBitmap()) {
            positionY(mainHeight * 1 / 5)
            centerXOnStage()
        }

        val idText = tamraText("ID를 입력하세요.", color = Colors.BLACK) {
            alignTopToBottomOf(logo, 30)
            centerXOnStage()
        }

        val idInput = tamraText("", color = Colors.BLACK) {
            alignTopToBottomOf(idText, 20)
            centerXOnStage()
        }

        // start
        fixedSizeContainer(200, 40) {
            alignTopToBottomOf(idInput, 50)
            centerXOnStage()
            val bg = tamraRect(width, height, color = Colors.DIMGREY)
            tamraText("시작하기", textSize = 24.0, color = Colors.WHITE) {
                centerOn(bg)
            }
            tamraRect(width, height, color = Colors.TRANSPARENT_WHITE) {
                alpha = 0.1
                onOut { alpha = 0.1 }
                onOver { alpha = 0.5 }
                onClick {
                    sceneContainer.changeTo<WorldScene>()
                }
            }
        }

        keyboard {
            if (it == BACKSPACE) {
                if (idInput.text.isNotEmpty()) {
                    idInput.text = idInput.text.substr(0, idInput.text.length - 1)
                }
            } else {
                idInput.text += it
            }
            idInput.centerXOnStage()
        }
    }
}