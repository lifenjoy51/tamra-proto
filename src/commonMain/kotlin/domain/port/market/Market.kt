package domain.port.market

import domain.Fleet
import domain.GameData
import domain.PortId
import domain.ProductId

data class MarketProductState(
    val marketSize: Int,
    var marketStock: Int,   // 플레이어가 구매 가능한 수량.
    var supplyAndDemand: Int    // 시세를 결정하는 수요공급 값.
)

data class CargoItem(
    val productId: ProductId,
    val price: Int,
    var quantity: Int
) {
    val name: String get() = GameData.getProduct(productId).name
}

data class MarketProduct(
    val id: ProductId,
    val state: MarketProductState
) {
    val marketPrice
        get() : Int {
            val product = GameData.getProduct(id)
            return product.price - (product.price * state.supplyAndDemand / state.marketSize)
        }

    fun supply(quantity: Int) {
        state.marketStock += quantity
        state.supplyAndDemand += quantity
    }

    fun consume(quantity: Int) {
        state.marketStock -= quantity
        state.supplyAndDemand -= quantity
    }
}

class Market(
    val portId: PortId,
    val marketProducts: Map<ProductId, MarketProduct>
) {
    val sellingProducts get() = marketProducts.values.filter { it.state.supplyAndDemand > 0 }

    fun price(id: ProductId): Int {
        return marketProducts.getValue(id).marketPrice
    }

    fun buy(fleet: Fleet, cart: MarketBuyCart) {
        // 카트에 있는 상품을 배에 싣기.
        val items = (fleet.cargoItems + cart.items)
            .groupBy { it.productId to it.price }
            .map { (pair, list) ->
                CargoItem(
                    productId = pair.first,
                    price = pair.second,
                    quantity = list.sumOf { it.quantity }
                )
            }
        fleet.cargoItems.removeAll { true }
        fleet.cargoItems.addAll(items)

        fleet.balance -= cart.totalPrice
        cart.items.forEach { item ->
            marketProducts.getValue(item.productId).consume(item.quantity)
        }
    }

    fun sell(fleet: Fleet, cart: MarketSellCart) {
        // 배에 있는 물건을 팔자..
        cart.items.forEach { cartItem ->
            fleet.cargoItems.first { cargo -> cartItem.productId == cargo.productId && cartItem.price == cargo.price }
                .apply { quantity -= cartItem.quantity }
        }
        fleet.cargoItems.removeAll { it.quantity == 0 }

        fleet.balance += cart.totalPrice
        cart.items.forEach { item ->
            marketProducts.getValue(item.productId).supply(item.quantity)
        }
    }
}

class MarketBuyCart(
    val market: Market,
    val fleet: Fleet
) {
    val items: MutableList<CargoItem> = mutableListOf()
    val fee = 0.1
    val totalPrice: Int get() = items.map { it.price * it.quantity * (1 + fee) }.sum().toInt()
    val totalQuantity: Int get() = items.map { it.quantity }.sum()

    fun getProductQuantity(id: ProductId): Int = items.firstOrNull { it.productId == id }?.quantity ?: 0

    fun addItem(productId: ProductId) {
        if (totalQuantity >= fleet.availableCargoSpace) return
        val price = market.price(productId)
        if (totalPrice + price >= fleet.balance) return
        items.firstOrNull { it.productId == productId && it.price == price }
            ?.let { it.quantity += 1 }
            ?: items.add(
                CargoItem(
                    productId = productId,
                    price = price,
                    quantity = 1
                )
            )
    }

    fun removeItem(productId: ProductId) {
        val price = market.price(productId)
        items.firstOrNull { it.productId == productId && it.price == price }
            ?.let {
                if (it.quantity > 0) {
                    it.quantity -= 1
                }
            }
    }
}

class MarketSellCart(
    val market: Market,
    val fleet: Fleet
) {
    val items: MutableList<CargoItem> = mutableListOf()
    val fee = 0.1
    val totalPrice: Int get() = items.map { market.price(it.productId) * it.quantity * (1 - fee) }.sum().toInt()
    val totalQuantity: Int get() = items.map { it.quantity }.sum()

    fun getProductQuantity(item: CargoItem): Int = items.firstOrNull { it.productId == item.productId && it.price == item.price }?.quantity
        ?: 0

    fun addItem(item: CargoItem) {
        if (getProductQuantity(item) >= item.quantity) return
        items.firstOrNull { it.productId == item.productId && it.price == item.price }
            ?.let { it.quantity += 1 }
            ?: items.add(
                CargoItem(
                    productId = item.productId,
                    price = item.price,
                    quantity = 1
                )
            )
    }

    fun removeItem(productId: ProductId, itemPrice: Int) {
        items.firstOrNull { it.productId == productId && it.price == itemPrice }
            ?.let {
                if (it.quantity > 0) {
                    it.quantity -= 1
                }
            }
    }
}