package domain

import domain.port.market.CargoItem
import domain.port.market.Market
import domain.port.shipyard.ShipBlueprint
import domain.port.shipyard.Shipyard

// static immutable game data object.
class GameData(
    private val ports: Map<PortId, Port>,
    private val products: Map<ProductId, Product>,
    private val shipBlueprints: Map<ShipType, ShipBlueprint>
) {
    companion object {
        private lateinit var instance: GameData
        fun init(
            ports: Map<PortId, Port>,
            products: Map<ProductId, Product>,
            shipBlueprints: Map<ShipType, ShipBlueprint>
        ) {
            instance = GameData(
                products = products,
                ports = ports,
                shipBlueprints = shipBlueprints
            )
        }

        val ports get() = instance.ports
        val products get() = instance.products
        val blueprints get() = instance.shipBlueprints

        fun getProduct(id: ProductId) = products.getValue(id)
        fun getBlueprint(type: ShipType) = blueprints.getValue(type)
    }
}

class Fleet(
    val ships: MutableList<Ship>,   // 플레이어의 배 목록
    var balance: Int,
    var port: PortId?,  // 현재 정박중인 항구.
    var location: XY,    // 현재 위치.
    var cargoItems: MutableList<CargoItem>
) {
    val totalCargoSpace: Int get() = ships.sumOf { it.cargoSize }
    val totalCargoQuantity: Int get() = cargoItems.sumOf { it.quantity }
    val availableCargoSpace: Int get() = totalCargoSpace - totalCargoQuantity

    fun getCargos(productId: ProductId): List<CargoItem> = cargoItems.filter { it.productId == productId }

    fun getCargos(productId: ProductId, price: Int): List<CargoItem> = getCargos(productId).filter { it.price == price }

    fun getCargosQuantity(productId: ProductId, price: Int): Int = getCargos(productId, price).sumOf { it.quantity }

}

class Port(
    val id: PortId,
    val name: String,
    val market: Market,
    val shipYard: Shipyard,
)

class Ship(
    val type: ShipType,
    val cargoSize: Int,
    val speed: Double,
    val name: String,
) {
    val priceForSale get() = GameData.getBlueprint(type).price / 2
}

class Product(
    val id: ProductId,
    val type: ProductType,
    val name: String,
    val price: Int,
)