package scene.port.market

import domain.GameStore
import domain.ProductId
import domain.port.market.CargoItem
import domain.port.market.Market
import domain.port.market.MarketSellCart
import scene.port.PortScene
import ui.LiveData

class MarketSellViewModel(private val store: GameStore) {
    // init
    private lateinit var market: Market
    private lateinit var marketSellCart: MarketSellCart

    // live data
    val cargoList: LiveData<List<CargoItem>> = LiveData(null)
    val cart: LiveData<MarketSellCart> = LiveData(null)

    fun init() {
        market = store.port()!!.market
        marketSellCart = MarketSellCart(
            market = market,
            fleet = store.fleet
        )

        cargoList(store.fleet.cargoItems)
        cart(marketSellCart)
    }

    fun clear() {
        cargoList.clear()
        cart.clear()
    }

    fun increaseQuantity(item: CargoItem) {
        marketSellCart.addItem(item)
        cart(marketSellCart)
    }

    fun decreaseQuantity(item: CargoItem) {
        marketSellCart.removeItem(item.productId, item.price)
        cart(marketSellCart)
    }

    fun getMarketPrice(productId: ProductId): Int {
        return market.price(productId)
    }

    suspend fun sell(changePortScene: suspend () -> PortScene) {
        market.sell(store.fleet, marketSellCart)
        changePortScene.invoke()
    }
}