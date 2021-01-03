package scene.common

import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.slice
import com.soywiz.korim.color.Colors
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korma.geom.vector.rectHole
import defaultMargin
import domain.port.shipyard.maxShipSpace
import ui.tamraButton
import ui.tamraRect
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
            solidRect(width, height, color = Colors.CADETBLUE) {
                centerOnStage()
            }

            // 테두리
            sgraphics {
                stroke(Colors.DARKSLATEGREY, StrokeInfo(thickness = 2.0)) {
                    rectHole(-1, -1, windowWidth + 1, windowHeight + 1)
                }
            }

            // head control
            val controls = fixedSizeContainer(width, defaultMargin * 4.0) {
                tamraButton(text = "X", textSize = 10.0, width = 20.0, height = 20.0) {
                    alignRightToRightOf(this@fixedSizeContainer, defaultMargin)
                    onClick { vm.toggleFleetInfo(false) }
                }
            }

            // cargo area
            val cargoTitle = fixedSizeContainer(width, 30.0) {
                alignTopToBottomOf(controls)
                tamraRect(width - 1, height, color = Colors.DARKOLIVEGREEN)
                tamraText("화물 정보")
                tamraText("000/000") {
                    alignRightToRightOf(this@fixedSizeContainer, defaultMargin)
                    vm.fleet.observe {
                        text = "${it.totalCargoQuantity} / ${it.totalCargoSpace}"
                    }
                }
            }
            val cargoArea = fixedSizeContainer(width, 150.0) {
                alignTopToBottomOf(cargoTitle)
                tamraRect(width - 1, height, color = Colors.DARKCYAN)

                val cargoHeader = fixedSizeContainer(width, 30.0) {
                    tamraText("")
                    tamraText("수량", ax = 140)
                    tamraText("가격", ax = 180)
                }

                fixedSizeContainer(width, 120.0) {
                    alignTopToBottomOf(cargoHeader)

                    vm.fleet.observe { fleet ->
                        fleet.cargoItems.sortedByDescending { it.quantity }.forEachIndexed { index, cargoItem ->
                            tamraText("${cargoItem.name}", ax = 0, ay = 30 * index)
                            tamraText("${cargoItem.quantity}", ax = 140, ay = 30 * index)
                            tamraText("${cargoItem.price}", ax = 180, ay = 30 * index)
                        }
                    }
                }
            }

            // ship area
            val shipTitle = fixedSizeContainer(width, 30.0) {
                alignTopToBottomOf(cargoArea)
                tamraRect(width - 1, height, color = Colors.SADDLEBROWN)
                tamraText("배 정보")
                tamraText("00/00") {
                    alignRightToRightOf(this@fixedSizeContainer, defaultMargin)
                    vm.fleet.observe {
                        text = "${it.ships.size} / $maxShipSpace"
                    }
                }
            }
            val shipArea = fixedSizeContainer(width, 90.0) {
                alignTopToBottomOf(shipTitle)
                tamraRect(width - 1, height, color = Colors.DARKGOLDENROD)

                val left = fixedSizeContainer(width / 2, height) {
                    val leftAreaWidth = width
                    val leftAreaHeight = height
                    sprite {
                        vm.selectedShip.observe {
                            bitmap = it.sprite.slice()
                            positionX((leftAreaWidth - bitmap.width) / 2)
                            positionY((leftAreaHeight - bitmap.height) / 2)
                        }
                    }
                }
                fixedSizeContainer(width / 2, height) {
                    alignLeftToRightOf(left)

                    tamraText("", ax = 0, ay = 0) {
                        vm.selectedShip.observe { text = it.name }
                    }

                    tamraText("", ax = 0, ay = defaultMargin * 3) {
                        vm.selectedShip.observe { text = "속도 : ${it.speed}" }
                    }

                    tamraText("", ax = 0, ay = defaultMargin * 6) {
                        vm.selectedShip.observe { text = "적재 : ${it.cargoSize}" }
                    }
                }
            }

            // ship list.
            val size = 5
            val cellWidth = windowWidth / 3.0
            uiHorizontalScrollableArea(contentWidth = cellWidth * size, height = 60.0, contentHeight = 60.0) {
                tamraRect(width, height, color = Colors.DARKSLATEGREY)

                this.parent?.apply {
                    positionX(0)
                    positionY(windowHeight - height)
                }

                vm.fleet.observe {
                    it.ships.forEachIndexed { i, ship ->
                        // ship info cell.
                        tamraButton(text = ship.name, px = (i * cellWidth + defaultMargin).toInt()) {
                            onClick { vm.selectShip(ship) }
                        }
                    }
                }
            }
        }

        // init vm
        vm.init()
    }
}