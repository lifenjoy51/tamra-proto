package scene.port.market

import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import defaultMargin
import domain.GameData
import domain.market.MarketProduct
import infoAreaHeight
import itemAreaHeight
import mainHeight
import mainWidth
import scene.port.PortScene
import textTabSpace
import ui.*

class MarketBuyView(private val vm: MarketBuyViewModel, private val changePortScene: suspend () -> PortScene) {
    private val itemView = MarketBuyItemView(vm)

    fun draw(container: Container) {
        container.apply {

            solidRect(width = mainWidth, height = mainHeight * 6 / 10, color = Colors.CORAL)

            // 선단 적재공간 영역.
            fixedSizeContainer(width = mainWidth, height = infoAreaHeight) {
                //
                solidRect(width = mainWidth, height = infoAreaHeight, color = Colors.CHOCOLATE) {}
                positionY(-infoAreaHeight)

                tamraText("000/000") {
                    vm.cart.observe { cart ->
                        text = "${cart.fleet.totalCargoQuantity + cart.totalQuantity} / ${cart.fleet.totalCargoSpace}"
                    }
                }
            }

            // 상품영역.
            uiVerticalScrollableArea(width = mainWidth.toDouble(), height = mainHeight * 4 / 10.0,
                contentWidth = mainWidth.toDouble(), contentHeight = mainHeight * 6 / 10.0) {
                // list?
                vm.sellingProducts.observe {
                    // 공급이 있어야 판매목록에 노출.
                    it.forEachIndexed { index, marketProduct ->
                        val item = fixedSizeContainer(mainWidth, itemAreaHeight) {
                            positionY(index * itemAreaHeight)
                        }
                        itemView.draw(item, marketProduct)
                    }
                }
            }

            // 정산 영역.
            fixedSizeContainer(width = mainWidth, height = mainHeight * 2 / 10) {
                positionY(mainHeight * 4 / 10)
                solidRect(width = mainWidth, height = mainHeight * 2 / 10, color = Colors.CHOCOLATE) {}

                // 계산...
                tamraText("자산")
                tamraText("000000", ax = itemAreaHeight) {
                    vm.cart.observe { text = it.fleet.balance.toString() }
                }

                tamraText("가격", ay = 25)
                tamraText("000000", ax = textTabSpace, ay = 25) {
                    vm.cart.observe { text = it.totalPrice.toString() }
                }

                tamraText("합계", px = defaultMargin, ay = 60)
                tamraText("000000", ax = textTabSpace, ay = 60) {
                    vm.cart.observe { text = (it.fleet.balance - it.totalPrice).toString() }
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
}

// 상품 각 row.
class MarketBuyItemView(private val vm: MarketBuyViewModel) {
    fun draw(container: FixedSizeContainer, marketProduct: MarketProduct) {
        container.apply {
            val product = GameData.getProduct(marketProduct.id)
            // 이름
            tamraText(product.name)
            // 시세 %
            tamraText("${(marketProduct.marketPrice * 100 / product.price)}%", ax = 50)
            // 가격
            tamraText("$${marketProduct.marketPrice}", ay = 25)

            // 수량 감소
            tamraText("-", textSize = 30.0, px = mainWidth - 150 + 10, vc = container)
            tamraRect(width = 40.0, height = 60.0, color = RGBA(0, 0, 0, 0), px = mainWidth - 150, vc = container) {
                onClick { vm.decreaseQuantity(product.id) }
            }

            // 수량
            tamraText("000/999", px = mainWidth - 120 + 2, vc = container) {
                val marketStock = marketProduct.state.marketStock
                vm.cart.observe {
                    text = "${it.getProductQuantity(product.id).pad(3)}/${marketStock.pad(3)}"
                }
            }

            // 수량 증가
            tamraText("+", textSize = 30.0, px = mainWidth - 50 + 10, vc = container)
            tamraRect(width = 40.0, height = 60.0, color = RGBA(0, 0, 0, 0), px = mainWidth - 60, vc = container) {
                onClick { vm.increaseQuantity(product.id) }
            }

        }
    }
}