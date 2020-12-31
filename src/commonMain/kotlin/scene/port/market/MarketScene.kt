package scene.port.market

import ViewModelProvider
import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onOut
import com.soywiz.korge.input.onOver
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import domain.*
import mainHeight
import mainWidth
import scene.port.PortScene
import ui.LiveData
import ui.tamraButton
import ui.tamraText
import ui.uiVerticalScrollableArea

class MarketScene(private val store: GameStore, viewModelProvider: ViewModelProvider) : Scene() {

    private val vm = viewModelProvider.marketViewModel
    private val buyView = MarketBuyView(vm)
    private val sellView = MarketSellView(vm)

    override suspend fun Container.sceneInit() {
        val background = solidRect(width = mainWidth, height = mainHeight)

        // draw ui..
        val defaultMargin = 5.0
        val buyArea = fixedSizeContainer(mainWidth, mainHeight * 6 / 10) {
            positionY(mainHeight * 4 / 10)
        }
        buyView.draw(buyArea)

        tamraText(
            text = "", textSize = 24.0, color = Colors.BLACK
        ).apply {
            alignX(background, 0.02, true)
            positionY(defaultMargin)
            vm.money.observe { text = it.toString() }
        }

        val marketTitle = tamraText("시장", color = Colors.BLACK, textSize = 24.0) {
            alignX(background, 0.5, true)
            positionY(defaultMargin)
        }

        tamraText("X", color = Colors.BLACK, textSize = 24.0) {
            alignX(background, 0.98, true)
            positionY(defaultMargin)
            alpha = 0.7
            onOut { this.alpha = 0.7 }
            onOver { this.alpha = 1.0 }
            onClick {
                sceneContainer.changeTo<PortScene>()
            }
        }

        tamraButton(
            text = "구매",
            width = mainWidth / 2.0
        ).apply {
            alignX(background, 0.0, true)
            positionY(marketTitle.height + defaultMargin * 2)
            onClick {
                println("buy")
            }
        }

        tamraButton(
            text = "판매",
            width = mainWidth / 2.0
        ).apply {
            alignX(background, 1.0, true)
            positionY(marketTitle.height + defaultMargin * 2)
            onClick {
                println("sell")

            }
        }

        // init vm
        vm.init()
    }
}

class MarketBuyView(val vm: MarketViewModel) {
    val itemView = MarketBuyItemView(vm)

    fun draw(container: Container) {
        container.apply {
            val background = solidRect(width = mainWidth, height = mainHeight * 6 / 10, color = Colors.CORAL)
            // 상품영역.
            uiVerticalScrollableArea(width = mainWidth.toDouble(), height = mainHeight * 4 / 10.0,
                contentWidth = mainWidth.toDouble(), contentHeight = mainHeight * 6 / 10.0) {

                // list?
                vm.market.observe {
                    it.saleProducts.forEachIndexed { index, product ->
                        val item = fixedSizeContainer(mainWidth, 60) {
                            positionY(index * 60)
                        }
                        itemView.draw(item, index, product)
                    }
                }
            }
            // 정산 영역.
            fixedSizeContainer(width = mainWidth, height = mainHeight * 2 / 10) {
                positionY(mainHeight * 4 / 10)
                solidRect(width = mainWidth, height = mainHeight * 2 / 10, color = Colors.CHOCOLATE) {}

                // 계산...
                val margin = 10
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
                        println("ok")
                    }
                }
                tamraButton(text = "취소", width = 60.0, height = 60.0).apply {
                    positionX(mainWidth - 140)
                    alignY(this@fixedSizeContainer, 0.5, true)
                    onClick {
                        println("cancel")
                    }
                }
            }

        }
    }
}


class MarketBuyItemView(val vm: MarketViewModel) {
    fun draw(container: FixedSizeContainer, index: Int, product: Product) {
        val margin = 10
        container.apply {
            tamraText(text = product.name) {
                positionX(margin)
                positionY(margin)
            }
            tamraText(text = product.price.toString()) {
                positionX(margin)
                positionY(margin + 25)
            }
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

            tamraText(
                text = "00"
            ).apply {
                positionX(mainWidth - 80 + 2)
                alignY(container, 0.5, true)
                vm.buyCart.observe {
                    text = (it[product.id] ?: 0).toString()
                }
            }

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


class MarketSellView(val vm: MarketViewModel)

class MarketViewModel(private val store: GameStore) {
    val money: LiveData<Int> = LiveData(null)
    val market: LiveData<Market> = LiveData(null)
    val buyCart: LiveData<MutableMap<ProductId, Int>> = LiveData(mutableMapOf())

    fun init() {
        initMoney()
        market(GameData.ports[store.port]!!.market)
        buyCart(mutableMapOf())
    }

    fun initMoney() {
        money(store.money)
    }

    fun increaseQuantity(product: Product) {
        val cart = buyCart.value!!
        val quantity = cart[product.id] ?: 0
        cart[product.id] = (quantity + 1).coerceIn(0, 99)
        buyCart(cart)
    }

    fun decreaseQuantity(product: Product) {
        val cart = buyCart.value!!
        val quantity = cart[product.id] ?: 0
        cart[product.id] = (quantity - 1).coerceIn(0, 99)
        buyCart(cart)
    }

    fun calculateCartPrice(cart: MutableMap<ProductId, Int>): Int {
        return cart.map { (k, v) -> GameData.products.getValue(k).price * v }.sum()
    }
}