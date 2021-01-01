package scene.port.market

import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import domain.MarketProduct
import mainHeight
import mainWidth
import ui.tamraButton
import ui.tamraText
import ui.uiVerticalScrollableArea

class MarketBuyView(val vm: MarketViewModel) {
    private val itemView = MarketBuyItemView(vm)
    val margin = 10

    fun draw(container: Container) {
        container.apply {
            val infoHeight = 30

            val background = solidRect(width = mainWidth, height = mainHeight * 6 / 10, color = Colors.CORAL)
            // 선단 영역.
            fixedSizeContainer(width = mainWidth, height = infoHeight) {
                //
                solidRect(width = mainWidth, height = infoHeight, color = Colors.CHOCOLATE) {}
                positionY(-infoHeight)

                tamraText("000/000") {
                    positionX(margin)
                    positionY(margin)
                    var cargoMax = 0
                    vm.buyCart.observe { cart ->
                        val total = cart.values.sum()
                        text = "$total / $cargoMax"
                    }
                    vm.cargoMaxSize.observe { it ->
                        cargoMax = it
                    }
                }

            }

            // 상품영역.
            uiVerticalScrollableArea(width = mainWidth.toDouble(), height = mainHeight * 4 / 10.0,
                contentWidth = mainWidth.toDouble(), contentHeight = mainHeight * 6 / 10.0) {
                // list?
                vm.market.observe {
                    it.marketProducts.values.filter { mp -> mp.marketState.supplyAndDemand > 0 }.forEachIndexed { index, marketProduct ->
                        val item = fixedSizeContainer(mainWidth, 60) {
                            positionY(index * 60)
                        }
                        itemView.draw(item, index, marketProduct)
                    }
                }
            }

            // 정산 영역.
            fixedSizeContainer(width = mainWidth, height = mainHeight * 2 / 10) {
                positionY(mainHeight * 4 / 10)
                solidRect(width = mainWidth, height = mainHeight * 2 / 10, color = Colors.CHOCOLATE) {}

                // 계산...
                tamraText("자산") {
                    positionX(margin)
                    positionY(margin)
                }
                tamraText("000000") {
                    positionX(margin + 60)
                    positionY(margin)
                    vm.buyCart.observe {
                        text = vm.money.value!!.toString()
                    }
                }

                tamraText("가격") {
                    positionX(margin)
                    positionY(margin + 25)
                }
                tamraText("000000") {
                    positionX(margin + 60)
                    positionY(margin + 25)
                    vm.buyCart.observe {
                        val p: Int = vm.calculateCartPrice(it)
                        text = p.toString()
                    }
                }

                tamraText("합계") {
                    positionX(margin)
                    positionY(margin + 60)
                }
                tamraText("000000") {
                    positionX(margin + 60)
                    positionY(margin + 60)
                    vm.buyCart.observe {
                        val money = vm.money.value!!
                        val price = vm.calculateCartPrice(it)
                        text = (money - price).toString()
                    }
                }

                // 버튼영역.
                tamraButton(text = "결정", width = 60.0, height = 60.0).apply {
                    positionX(mainWidth - 70)
                    alignY(this@fixedSizeContainer, 0.5, true)
                    onClick {
                        vm.buy()
                        vm.changeToPort()
                    }
                }
                tamraButton(text = "취소", width = 60.0, height = 60.0).apply {
                    positionX(mainWidth - 140)
                    alignY(this@fixedSizeContainer, 0.5, true)
                    onClick {
                        vm.init()
                    }
                }
            }

        }
    }
}


class MarketBuyItemView(val vm: MarketViewModel) {
    private val margin = 10
    fun draw(container: FixedSizeContainer, index: Int, marketProduct: MarketProduct) {
        container.apply {
            val product = marketProduct.product
            // 상품정보
            tamraText(text = product.name) {
                positionX(margin)
                positionY(margin)
            }
            tamraText(text = marketProduct.price.toString()) {
                positionX(margin)
                positionY(margin + 25)
            }

            tamraText(text = marketProduct.marketState.marketSize.toString()) {
                positionX(margin + 50)
                positionY(margin)
                val marketStock = marketProduct.marketState.marketStock
                vm.buyCart.observe {
                    val productCount = it.filter { (k, v) -> k == product.id }.values.sum()
                    text = "${marketStock - productCount} / $marketStock"
                }
            }
            val pricePercent = (marketProduct.price * 100 / product.price)
            tamraText(text = "$pricePercent%") {
                positionX(margin + 50)
                positionY(margin + 25)
            }

            // 수량 감소
            tamraText(
                text = "-",
                textSize = 30.0
            ).apply {
                positionX(mainWidth - 120 + 10)
                alignY(container, 0.5, true)
            }
            solidRect(width = 40.0, height = 60.0, color = RGBA(0, 0, 0, 0)) {
                positionX(mainWidth - 120)
                alignY(container, 0.5, true)
                onClick {
                    vm.decreaseQuantity(product)
                }
            }

            // 수량
            tamraText(
                text = "00"
            ).apply {
                positionX(mainWidth - 80 + 2)
                alignY(container, 0.5, true)
                vm.buyCart.observe {
                    text = (it[product.id] ?: 0).toString()
                }
            }

            // 수량 증가
            tamraText(
                text = "+",
                textSize = 30.0
            ).apply {
                positionX(mainWidth - 60 + 10)
                alignY(container, 0.5, true)
            }
            solidRect(width = 40.0, height = 60.0, color = RGBA(0, 0, 0, 0)) {
                positionX(mainWidth - 60)
                alignY(container, 0.5, true)
                onClick {
                    vm.increaseQuantity(product)
                }
            }

        }
    }
}