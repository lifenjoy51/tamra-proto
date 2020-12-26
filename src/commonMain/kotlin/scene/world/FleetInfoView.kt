package scene.world

import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onOut
import com.soywiz.korge.input.onOver
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.slice
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korma.geom.vector.rect
import domain.GameContext
import domain.GameData
import mainHeight
import mainWidth
import ui.tamraText
import ui.uiHorizontalScrollableArea
import windowHeight
import windowWidth

class FleetInfoView(
    private val context: GameContext,
    private val vm: FleetInfoViewModel,
    private val worldViewModel: WorldViewModel
) {
    fun draw(container: Container) {
        container.apply {
            val layer = this
            visible = false
            val back = solidRect(windowWidth, windowHeight, color = RGBA(0, 187, 255, 240)) {
                centerOnStage()
            }
            sgraphics {
                stroke(Colors.DIMGREY, StrokeInfo(thickness = 2.0)) {
                    rect((mainWidth - windowWidth) / 2.0, (mainHeight - windowHeight) / 2.0, windowWidth.toDouble(), windowHeight.toDouble())
                }
            }

            tamraText("X", color = Colors.BLACK, textSize = 24.0) {
                alignX(back, 0.98, true)
                alignY(back, 0.02, true)
                alpha = 0.7
                onOut { this.alpha = 0.7 }
                onOver { this.alpha = 1.0 }
                onClick {
                    worldViewModel.toggleFleetInfo(false)
                }
            }

            var defaultShip = context.ships.first()

            image(texture = GameData.blueprints.get(defaultShip.type)!!.imgSprite) {
                positionX(55)
                positionY(100)
                vm.shipImage.observe {
                    bitmapSrc = it.slice()
                }
            }

            tamraText(defaultShip.name) {
                positionX(55)
                positionY(160)
                vm.shipName.observe { text = it }
            }

            tamraText(defaultShip.speed.toString()) {
                positionX(55)
                positionY(190)
                vm.shipSpeed.observe { text = it }
            }

            tamraText(GameData.blueprints.get(defaultShip.type)!!.typeName) {
                positionX(55)
                positionY(220)
                vm.shipTypeName.observe { text = it }
            }

            tamraText(defaultShip.cargos.values.joinToString("\n") {
                GameData.products[it]!!.name
            }) {
                positionX(mainWidth / 2)
                positionY(100)
                vm.shipCargos.observe { text = it }
            }

            val size = 10
            val cellWidth = windowWidth / 3.0
            uiHorizontalScrollableArea(contentWidth = cellWidth * size) {
                this.parent?.apply {
                    positionX((mainWidth - windowWidth) / 2)
                    positionY(windowHeight + mainHeight / 10 - height)
                }
                context.ships.forEachIndexed { i, ship ->
                    // ship info cell.
                    fixedSizeContainer(cellWidth, height) {
                        val cellContainer = this
                        positionX(i * cellWidth)
                        centerYOn(cellContainer)
                        solidRect(cellWidth * 0.9, height * 0.9, color = Colors.CORAL) {
                            centerOn(cellContainer)
                        }
                        tamraText(ship.name) {
                            centerOn(cellContainer)
                        }
                        onClick {
                            GameData.blueprints.getValue(ship.type).apply {
                                vm.shipImage(imgSprite)
                                vm.shipTypeName(typeName)
                            }
                            vm.shipName(ship.name)
                            vm.shipSpeed(ship.speed.toString())
                            vm.shipCargos(ship.cargos.values.joinToString("\n") {
                                GameData.products[it]!!.name
                            })
                        }
                    }

                }
            }
        }
    }
}