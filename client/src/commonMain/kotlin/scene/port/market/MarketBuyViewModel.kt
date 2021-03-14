package scene.port.market

import scene.port.PortScene
import tamra.common.GameStore
import tamra.common.ProductId
import tamra.port.market.Market
import tamra.port.market.MarketBuyCart
import tamra.port.market.MarketProduct
import ui.LiveData

class MarketBuyViewModel(private val store: GameStore) {
    // init
    private lateinit var market: Market
    private lateinit var marketBuyCart: MarketBuyCart

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
        market.buy(store.fleet, marketBuyCart)
        changePortScene()
    }
}