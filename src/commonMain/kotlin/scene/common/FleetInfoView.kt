package scene.common

import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.Bitmap1
import com.soywiz.korim.bitmap.slice
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korma.geom.vector.rect
import defaultMargin
import mainHeight
import mainWidth
import ui.tamraButton
import ui.tamraImage
import ui.tamraText
import ui.uiHorizontalScrollableArea
import windowHeight
import windowWidth

class FleetInfoView(
    private val vm: FleetInfoViewModel
) {
    fun draw(container: Container) {
        container.apply {
            visible = false

            // 배경
            solidRect(windowWidth, windowHeight, color = RGBA(0, 187, 255, 240)) {
                centerOnStage()
            }
            // 테두리
            sgraphics {
                stroke(Colors.DIMGREY, StrokeInfo(thickness = 2.0)) {
                    rect((mainWidth - windowWidth) / 2.0, (mainHeight - windowHeight) / 2.0, windowWidth.toDouble(), windowHeight.toDouble())
                }
            }

            tamraButton(text = "X", textSize = 10.0, width = 20.0, height = 20.0, px = mainWidth - 55, py = 65) {
                onClick { vm.toggleFleetInfo(false) }
            }

            tamraImage(texture = Bitmap1(0, 0), px = 55, py = 100) {
                vm.shipImage.observe { bitmapSrc = it.slice() }
            }

            tamraText("", px = 55, py = 160) {
                vm.shipName.observe { text = it }
            }

            tamraText("", px = 55, py = 190) {
                vm.shipSpeed.observe { text = it }
            }

            tamraText("", px = 55, py = 220) {
                vm.shipTypeName.observe { text = it }
            }

            tamraText("", px = mainWidth / 2, py = 100) {
                vm.shipCargos.observe { text = it }
            }

            val size = 10
            val cellWidth = windowWidth / 3.0
            uiHorizontalScrollableArea(contentWidth = cellWidth * size, height = 70.0) {
                this.parent?.apply {
                    positionX((mainWidth - windowWidth) / 2)
                    positionY(windowHeight + mainHeight / 10 - height + defaultMargin)
                }

                vm.playerShips.observe {
                    it.forEachIndexed { i, ship ->
                        // ship info cell.
                        tamraButton(text = ship.name, px = (i * cellWidth).toInt()) {
                            onClick { vm.selectShip(ship) }
                        }
                    }
                }
            }
        }

        // init vm
        vm.initPlayerShips()
    }
}