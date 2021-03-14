package tamra.common

import kotlinx.serialization.Serializable
import tamra.event.EventCondition
import tamra.event.GameEvent
import tamra.port.market.CargoItem
import tamra.port.market.Market
import tamra.port.shipyard.ShipBlueprint
import tamra.port.shipyard.Shipyard

// static immutable game data object.
class GameData(
    private val ports: Map<PortId, Port>,
    private val products: Map<ProductId, Product>,
    private val shipBlueprints: Map<ShipType, ShipBlueprint>,
    private val conditions: Map<String, EventCondition>,
    private val events: Map<String, GameEvent>
) {
    companion object {
        private lateinit var instance: GameData
        fun init(
            ports: Map<PortId, Port>,
            products: Map<ProductId, Product>,
            shipBlueprints: Map<ShipType, ShipBlueprint>,
            conditions: Map<String, EventCondition>,
            events: Map<String, GameEvent>
        ) {
            instance = GameData(
                products = products,
                ports = ports,
                shipBlueprints = shipBlueprints,
                conditions = conditions,
                events = events
            )
        }

        val ports get() = instance.ports
        val products get() = instance.products
        val blueprints get() = instance.shipBlueprints
        val conditions get() = instance.conditions
        val events get() = instance.events

        fun getProduct(id: ProductId) = products.getValue(id)
        fun getBlueprint(type: ShipType) = blueprints.getValue(type)
        fun getCondition(id: String): EventCondition = conditions.getValue(id)
    }
}

class Fleet(
    val ships: MutableList<Ship>,   // 플레이어의 배 목록
    var balance: Int,
    var port: PortId? = null,  // 현재 정박중인 항구.
    var landing: LandingId? = null, // 현재  상륙지점.
    var location: LocationXY,    // 현재 위치.
    var cargoItems: MutableList<CargoItem>
) {
    val totalCargoSpace: Int get() = ships.sumOf { it.cargoSize }
    val totalCargoQuantity: Int get() = cargoItems.sumOf { it.quantity }
    val availableCargoSpace: Int get() = totalCargoSpace - totalCargoQuantity

    // +E/-W +S/-N 좌표를 나타낸다.
    val getCoord: Coord get() = Coord(location.x / tileSize / tilesPerX, location.y / tileSize / tilesPerY)

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
    val imgName: String get() = GameData.getBlueprint(type).imgName
}

class Product(
    val id: ProductId,
    val type: ProductType,
    val name: String,
    val price: Int,
)

@Serializable
data class Site(
    val id: SiteId,
    val name: String,
    val subtitle: String,
)