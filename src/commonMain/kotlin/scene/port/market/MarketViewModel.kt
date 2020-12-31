package scene.port.market

import domain.*
import scene.port.PortScene
import ui.LiveData

class MarketViewModel(private val store: GameStore) {
    lateinit var changeToPort: suspend () -> PortScene
    val money: LiveData<Int> = LiveData(null)
    val market: LiveData<Market> = LiveData(null)
    val buyCart: LiveData<MutableMap<ProductId, Int>> = LiveData(mutableMapOf())
    val fleet: LiveData<MutableList<Ship>> = LiveData(null)
    val cargoMaxSize: LiveData<Int> = LiveData(null)

    fun init() {
        money(store.money)
        market(GameData.ports[store.port]!!.market)
        fleet.observe { ships ->
            cargoMaxSize(ships.sumOf { it.cargoSize } - ships.sumOf { it.cargos.size })
            buyCart(mutableMapOf())
        }
        fleet(store.ships)

    }

    fun increaseQuantity(product: Product) {
        if (isFullCart()) return
        val cart = buyCart.value!!
        val quantity = cart[product.id] ?: 0
        cart[product.id] = (quantity + 1).coerceIn(0, 99)
        buyCart(cart)
    }

    private fun isFullCart(): Boolean {
        val total = buyCart.value!!.values.sum()
        val max = cargoMaxSize.value!!
        return total >= max
    }

    fun decreaseQuantity(product: Product) {
        val cart = buyCart.value!!
        val quantity = cart[product.id] ?: 0
        cart[product.id] = (quantity - 1).coerceIn(0, 99)
        buyCart(cart)
    }

    fun calculateCartPrice(cart: MutableMap<ProductId, Int>): Int {
        return cart.map { (k, v) -> market.value!!.marketProducts.getValue(k).price * v }.sum()
    }

    fun buy() {
        // 카트에 있는 상품을 배에 싣기.
        val productPriceList = buyCart.value!!.flatMap { (k, v) ->
            MutableList(v) {
                PurchasedProduct(
                    id = k,
                    price = market.value!!.marketProducts.getValue(k).price
                )
            }
        }.toMutableList()

        val totalPrice = productPriceList.sumOf { it.price }

        while (productPriceList.isNotEmpty()) {
            store.ships.forEach { s ->
                if (s.cargos.size < s.cargoSize) {
                    productPriceList.removeFirstOrNull()?.let { pp ->
                        s.cargos.add(pp)
                    }
                }
            }
        }

        store.money -= totalPrice

        println("buy completed")
        // TODO 물가 변동, 재고 변동 적용 필요.
    }
}