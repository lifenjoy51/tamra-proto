package scene.port.market

import domain.GameStore
import domain.ProductId
import domain.market.Market
import domain.market.MarketBuyCart
import domain.market.MarketProduct
import scene.port.PortScene
import ui.LiveData

class MarketBuyViewModel(private val store: GameStore) {
    // init
    lateinit var market: Market
    lateinit var marketBuyCart: MarketBuyCart

    // live data
    val sellingProducts: LiveData<List<MarketProduct>> = LiveData(null)
    val cart: LiveData<MarketBuyCart> = LiveData(null)

    fun init() {
        market = store.port()!!.market
        marketBuyCart = MarketBuyCart(
            market = market,
            fleet = store.fleet
        )

        // livedata..
        sellingProducts(market.sellingProducts)
        cart(marketBuyCart)
    }

    fun clear() {
        sellingProducts.clear()
        cart.clear()
    }

    fun increaseQuantity(id: ProductId) {
        marketBuyCart.addItem(id)
        cart(marketBuyCart)
    }

    fun decreaseQuantity(id: ProductId) {
        marketBuyCart.removeItem(id)
        cart(marketBuyCart)
    }

    suspend fun buy(changePortScene: suspend () -> PortScene) {
        market.buy(marketBuyCart, store.fleet)
        changePortScene.invoke()
    }
}