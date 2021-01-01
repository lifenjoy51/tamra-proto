package scene.port.market

import domain.*
import scene.port.PortScene
import ui.LiveData

class MarketSellViewModel(private val store: GameStore) {
    // init
    val money: LiveData<Int> = LiveData(null)
    val marketProducts: LiveData<Map<ProductId, MarketProduct>> = LiveData(null)
    val ownedProducts: LiveData<List<GroupedPurchasedProduct>> = LiveData(null)
    val cargoMaxSize: LiveData<Int> = LiveData(null)
    val cargoSize: LiveData<Int> = LiveData(null)

    // var
    val cart: LiveData<MutableMap<ProductId, Int>> = LiveData(mutableMapOf())
    val cartPrice: LiveData<Int> = LiveData(null)
    val balance: LiveData<Int> = LiveData(null)

    fun init() {
        money(store.money)
        marketProducts(GameData.ports[store.port]!!.market.marketProducts)
        ownedProducts(store.ships
            .flatMap { it.cargos }
            .groupBy { it.id }
            .map { (id, list) ->
                GroupedPurchasedProduct(
                    product = GameData.products.getValue(id),
                    averagePrice = list.sumOf { it.price } / list.size,
                    count = list.size
                )
            })
        cargoMaxSize(store.ships.sumOf { it.cargoSize })
        cargoSize(store.ships.sumOf { it.cargos.size })
        cart(mutableMapOf())
        cartPrice(0)
        balance(store.money)
    }

    fun clear() {
        money.clear()
        marketProducts.clear()
        cargoMaxSize.clear()
        cargoSize.clear()
        cart.clear()
        cartPrice.clear()
        balance.clear()
    }

    private fun isValidCart(product: Product): Boolean {
        val productCount = ownedProducts.value!!.first { it.product == product }.count
        val total = cart.value!!.filterKeys { it == product.id }.values.sum()
        return total < productCount
    }

    fun increaseQuantity(product: Product) {
        if (!isValidCart(product)) return
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
        val price = cart.map { (k, v) -> marketProducts.value!!.getValue(k).price * v }.sum()
        cartPrice((price * 0.95).toInt())
    }

    private fun calculateBalance() {
        balance(money.value!! + cartPrice.value!!)
    }

    suspend fun sell(changePortScene: suspend () -> PortScene) {
        // 카트에 있는 상품을 배에 싣기.
        val productPriceList = cart.value!!.flatMap { (id, count) ->
            MutableList(count) {
                PurchasedProduct(
                    id = id,
                    price = ownedProducts.value!!.first { it.product.id == id }.averagePrice
                )
            }
        }.toMutableList()

        while (productPriceList.isNotEmpty()) {
            store.ships.forEach { s ->
                productPriceList.firstOrNull()?.let { pp ->
                    val d = s.cargos.remove(pp)
                    if (d) productPriceList.removeFirst()
                }
            }
        }

        store.money = balance.value!!

        println("sell completed")
        changePortScene.invoke()

        // TODO 물가 변동, 재고 변동 적용 필요.
    }
}