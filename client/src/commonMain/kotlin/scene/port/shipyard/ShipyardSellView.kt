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
import ui.tamraText

class ShipyardSellView(
    private val vm: ShipyardSellViewModel,
    private val changePortScene: suspend () -> PortScene
) {

    fun draw(container: Container) {
        container.apply {
            solidRect(width = container.width, height = container.height, color = Colors.DARKGREY)

            // 선박수 영역.
            val shipQuantity = fixedSizeContainer(width = mainWidth, height = infoAreaHeight) {
                positionY(162)

                //
                solidRect(width = mainWidth, height = infoAreaHeight, color = Colors.DIMGRAY) {}

                tamraText("보유 선박")

                tamraText("0/0", px = mainWidth - 50) {
                    vm.fleet.observe { fleet ->
                        text = "${fleet.ships.size} / $maxShipSpace"
                    }
                }

            }

            // 상품영역.
            val shipList = fixedSizeContainer(width = mainWidth.toDouble(), height = infoAreaHeight * 5.0) {
                alignTopToBottomOf(shipQuantity)

                // list?
                vm.fleet.observe {
                    // 공급이 있어야 판매목록에 노출.
                    it.ships.forEachIndexed { index, ship ->
                        fixedSizeContainer(mainWidth, infoAreaHeight) {
                            positionY(index * infoAreaHeight)
                            // 이름
                            tamraText(ship.name).centerOn(this@fixedSizeContainer)

                            onOver { alpha = 0.5 }
                            onOut { alpha = 1.0 }

                            onClick {
                                vm.select(ship)
                            }

                            tamraText("O") {
                                visible = false
                                vm.selectedShip.observe { s ->
                                    visible = (s == ship)
                                }
                            }

                        }
                    }
                }
            }

            // 정산 영역.
            fixedSizeContainer(width = mainWidth, height = mainHeight * 2 / 10) {
                alignTopToBottomOf(shipList)


                solidRect(width = mainWidth, height = mainHeight * 2 / 10, color = Colors.DIMGRAY) {}

                // 계산...
                tamraText("자산")
                tamraText("000000", ax = itemAreaHeight) {
                    vm.fleet.observe { text = it.balance.toString() }
                }

                tamraText("가격", ay = 25)
                tamraText("", ax = textTabSpace, ay = 25) {
                    vm.selectedShip.observe { text = it.priceForSale.toString() }
                }

                tamraText("합계", px = defaultMargin, ay = 60)
                tamraText("", ax = textTabSpace, ay = 60) {
                    vm.selectedShip.observe { text = "${vm.fleet.get().balance + it.priceForSale}" }
                }

                // 버튼영역.
                tamraButton(text = "결정", width = 60.0, height = 60.0, px = mainWidth - 70, vc = this@fixedSizeContainer) {
                    onClick { vm.sell(changePortScene) }
                }
                tamraButton(text = "취소", width = 60.0, height = 60.0, px = mainWidth - 140, vc = this@fixedSizeContainer) {
                    onClick { vm.init() }
                }
            }
        }
    }
}