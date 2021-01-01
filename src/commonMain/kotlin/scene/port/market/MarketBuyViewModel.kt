package scene.port.market

import domain.*
import scene.port.PortScene
import ui.LiveData

class MarketBuyViewModel(private val store: GameStore) {
    // init
    lateinit var marketProducts: Map<ProductId, MarketProduct>
    val money: LiveData<Int> = LiveData(null)
    val availableProducts: LiveData<List<MarketProduct>> = LiveData(null)
    val cargoMaxSize: LiveData<Int> = LiveData(null)

    // var
    val cart: LiveData<MutableMap<ProductId, Int>> = LiveData(mutableMapOf())
    val cartPrice: LiveData<Int> = LiveData(null)
    val balance: LiveData<Int> = LiveData(null)

    fun init() {
        money(store.money)
        marketProducts = GameData.ports[store.port]!!.market.marketProducts
        availableProducts(marketProducts.values.filter { mp -> mp.marketState.supplyAndDemand > 0 })
        cargoMaxSize(store.ships.sumOf { it.cargoSize - it.cargos.size })
        cart(mutableMapOf())
        cartPrice(0)
        balance(store.money)
    }

    fun clear() {
        money.clear()
        availableProducts.clear()
        cargoMaxSize.clear()
        cart.clear()
        cartPrice.clear()
        balance.clear()
    }

    private fun isFullCart(): Boolean {
        val total = cart.value!!.values.sum()
        val max = cargoMaxSize.value!!
        return total >= max
    }

    fun increaseQuantity(product: Product) {
        if (isFullCart()) return
        val cart = cart.value!!
        val quantity = cart[product.id] ?: 0
        cart[product.id] = (quantity + 1).coerceIn(0, 99)
        cart(cart)
        calculateCartPrice(this.cart.value!!)
        calculateBalance()
    }

    fun decreaseQuantity(product: Product) {
        val cart = cart.value!!
        val quantity = cart[product.id] ?: 0
        cart[product.id] = (quantity - 1).coerceIn(0, 99)
        cart(cart)
        calculateCartPrice(this.cart.value!!)
        calculateBalance()
    }

    private fun calculateCartPrice(cart: MutableMap<ProductId, Int>) {
        val price = cart.map { (k, v) -> marketProducts.getValue(k).price * v }.sum()
        cartPrice((price * 1.05).toInt())
    }

    private fun calculateBalance() {
        balance(money.value!! - cartPrice.value!!)
    }

    suspend fun buy(changePortScene: suspend () -> PortScene) {
        // 카트에 있는 상품을 배에 싣기.
        val productPriceList = cart.value!!.flatMap { (k, v) ->
            MutableList(v) {
                PurchasedProduct(
                    id = k,
                    price = marketProducts.getValue(k).price
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
        changePortScene.invoke()

        // TODO 물가 변동, 재고 변동 적용 필요.
        // TODO 같은 물건의 가격이 다를 경우 평준화 작업 필요.
    }
}