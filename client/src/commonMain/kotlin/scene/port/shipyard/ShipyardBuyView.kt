package scene.port.shipyard

import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onOut
import com.soywiz.korge.input.onOver
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import scene.port.PortScene
import tamra.*
import tamra.port.shipyard.maxShipSpace
import ui.tamraButton
import ui.tamraRect
import ui.tamraText

class ShipyardBuyView(
    private val vm: ShipyardBuyViewModel,
    private val changePortScene: suspend () -> PortScene
) {

    fun draw(container: Container) {
        container.apply {
            solidRect(width = container.width, height = container.height, color = Colors.DARKGREEN)

            // 선택한 선박 정보
            val shipInfo = fixedSizeContainer(width = mainWidth, height = 222)
            drawShipDetail(shipInfo)

            // 선박수 영역.
            val shipQuantity = fixedSizeContainer(width = mainWidth, height = infoAreaHeight) {
                alignTopToBottomOf(shipInfo)

                //
                solidRect(width = mainWidth, height = infoAreaHeight, color = Colors.DARKSLATEGREY) {}

                tamraText("보유 선박")

                tamraText("0/0", px = mainWidth - 50) {
                    vm.fleet.observe { fleet ->
                        text = "${fleet.ships.size} / $maxShipSpace"
                    }
                }

            }

            // 상품영역.
            val shipList = fixedSizeContainer(width = mainWidth.toDouble(), height = infoAreaHeight * 3.0) {
                alignTopToBottomOf(shipQuantity)

                // list?
                vm.sellingShips.observe {
                    // 공급이 있어야 판매목록에 노출.
                    it.forEachIndexed { index, blueprint ->
                        fixedSizeContainer(mainWidth, infoAreaHeight) {
                            positionY(index * infoAreaHeight)
                            // 이름
                            tamraText(blueprint.typeName).centerOn(this@fixedSizeContainer)

                            onOver { alpha = 0.5 }
                            onOut { alpha = 1.0 }

                            onClick {
                                vm.select(blueprint)
                            }
                        }
                    }
                }
            }

            // 정산 영역.
            fixedSizeContainer(width = mainWidth, height = mainHeight * 2 / 10) {
                alignTopToBottomOf(shipList)


                solidRect(width = mainWidth, height = mainHeight * 2 / 10, color = Colors.DARKSLATEGREY) {}

                // 계산...
                tamraText("자산")
                tamraText("000000", ax = itemAreaHeight) {
                    vm.fleet.observe { text = it.balance.toString() }
                }

                tamraText("가격", ay = 25)
                tamraText("", ax = textTabSpace, ay = 25) {
                    vm.selectedBlueprint.observe { text = it.price.toString() }
                }

                tamraText("합계", px = defaultMargin, ay = 60)
                tamraText("", ax = textTabSpace, ay = 60) {
                    vm.selectedBlueprint.observe { text = "${vm.fleet.get().balance - it.price}" }
                }

                // 버튼영역.
                tamraButton(text = "결정", width = 60.0, height = 60.0, px = mainWidth - 70, vc = this@fixedSizeContainer) {
                    onClick { vm.buy(changePortScene) }
                }
                tamraButton(text = "취소", width = 60.0, height = 60.0, px = mainWidth - 140, vc = this@fixedSizeContainer) {
                    onClick { vm.init() }
                }
            }
        }
    }

    fun drawShipDetail(container: Container) {
        container.apply {
            visible = false
            vm.selectedBlueprint.observe {
                visible = true
            }

            // image layer
            val top = fixedSizeContainer(mainWidth.toDouble(), container.height * 2 / 5) {
                val topWidth = width
                val topHeight = height
                tamraRect(width, height, color = Colors.MIDNIGHTBLUE)
                sprite {
                    vm.selectedBlueprint.observe {
                        bitmap = spriteMap.getValue(it.imgName)
                        positionX((topWidth - bitmap.width) / 2)
                        positionY((topHeight - bitmap.height) / 2)
                    }
                }
            }

            // detail
            fixedSizeContainer(mainWidth.toDouble(), container.height * 3 / 5) {
                alignTopToBottomOf(top)
                val left = fixedSizeContainer(mainWidth / 4.0, container.height * 2 / 3) {
                    positionX(mainWidth / 4.0)
                    positionY(height / 10)

                    val textName = tamraText("이름")
                    val textCargoSize = tamraText("적재량").alignTopToBottomOf(textName, defaultMargin)
                    val textSpeed = tamraText("속도").alignTopToBottomOf(textCargoSize, defaultMargin)
                    val textPrice = tamraText("가격").alignTopToBottomOf(textSpeed, defaultMargin)

                }
                fixedSizeContainer(mainWidth / 4.0, container.height * 2 / 3) {
                    alignLeftToRightOf(left)
                    positionY(height / 10)

                    val textName = tamraText("이") {
                        vm.selectedBlueprint.observe {
                            text = it.typeName
                        }
                    }
                    val textCargoSize = tamraText("적재량") {
                        alignTopToBottomOf(textName, defaultMargin)
                        vm.selectedBlueprint.observe {
                            text = it.cargoSize.toString()
                        }
                    }
                    val textSpeed = tamraText("속도") {
                        alignTopToBottomOf(textCargoSize, defaultMargin)
                        vm.selectedBlueprint.observe {
                            text = it.speed.toString()
                        }
                    }
                    val textPrice = tamraText("가격") {
                        alignTopToBottomOf(textSpeed, defaultMargin)
                        vm.selectedBlueprint.observe {
                            text = it.price.toString()
                        }
                    }
                }
            }
        }
    }
}