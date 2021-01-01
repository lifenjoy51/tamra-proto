package scene.port.market

import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import defaultMargin
import domain.MarketProduct
import infoAreaHeight
import itemAreaHeight
import mainHeight
import mainWidth
import scene.port.PortScene
import textTabSpace
import ui.tamraButton
import ui.tamraRect
import ui.tamraText
import ui.uiVerticalScrollableArea

class MarketBuyView(private val vm: MarketViewModel, private val changePortScene: suspend () -> PortScene) {
    private val itemView = MarketBuyItemView(vm)

    fun draw(container: Container) {
        container.apply {

            val background = solidRect(width = mainWidth, height = mainHeight * 6 / 10, color = Colors.CORAL)
            // 선단 적재공간 영역.
            fixedSizeContainer(width = mainWidth, height = infoAreaHeight) {
                //
                solidRect(width = mainWidth, height = infoAreaHeight, color = Colors.CHOCOLATE) {}
                positionY(-infoAreaHeight)

                tamraText("000/000") {
                    var cargoMax = 0
                    vm.buyCart.observe { cart ->
                        text = "${cart.values.sum()} / $cargoMax"
                    }
                    vm.cargoMaxSize.observe { cargoMax = it }
                }
            }

            // 상품영역.
            uiVerticalScrollableArea(width = mainWidth.toDouble(), height = mainHeight * 4 / 10.0,
                contentWidth = mainWidth.toDouble(), contentHeight = mainHeight * 6 / 10.0) {
                // list?
                vm.market.observe {
                    // 공급이 있어야 판매목록에 노출.
                    it.marketProducts.values.filter { mp -> mp.marketState.supplyAndDemand > 0 }.forEachIndexed { index, marketProduct ->
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
                    vm.buyCart.observe { text = vm.money.value!!.toString() }
                }

                tamraText("가격", ay = 25)
                tamraText("000000", ax = textTabSpace, ay = 25) {
                    vm.buyCart.observe { text = vm.calculateCartPrice(it).toString() }
                }

                tamraText("합계", px = defaultMargin, ay = 60)
                tamraText("000000", ax = textTabSpace, ay = 60) {
                    vm.buyCart.observe { text = ((vm.money.value!!) - (vm.calculateCartPrice(it))).toString() }
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
class MarketBuyItemView(private val vm: MarketViewModel) {
    fun draw(container: FixedSizeContainer, marketProduct: MarketProduct) {
        container.apply {
            val product = marketProduct.product
            // 이름
            tamraText(product.name)
            // 재고
            tamraText(marketProduct.marketState.marketStock.toString(), ax = 50) {
                val marketStock = marketProduct.marketState.marketStock
                vm.buyCart.observe {
                    val productCount = it.filter { (k, v) -> k == product.id }.values.sum()
                    text = "${marketStock - productCount} / $marketStock"
                }
            }
            // 가격
            tamraText(marketProduct.price.toString(), ay = 25)
            // 시세 %
            tamraText("${(marketProduct.price * 100 / product.price)}%", ax = 50, ay = 25)

            // 수량 감소
            tamraText("-", textSize = 30.0, px = mainWidth - 120 + 10, vc = container)
            tamraRect(width = 40.0, height = 60.0, color = RGBA(0, 0, 0, 0), px = mainWidth - 120, vc = container) {
                onClick { vm.decreaseQuantity(product) }
            }

            // 수량
            tamraText("00", px = mainWidth - 80 + 2, vc = container) {
                vm.buyCart.observe { text = ((it[product.id] ?: 0).toString()) }
            }

            // 수량 증가
            tamraText("+", textSize = 30.0, px = mainWidth - 60 + 10, vc = container)
            tamraRect(width = 40.0, height = 60.0, color = RGBA(0, 0, 0, 0), px = mainWidth - 60, vc = container) {
                onClick { vm.increaseQuantity(product) }
            }

        }
    }
}