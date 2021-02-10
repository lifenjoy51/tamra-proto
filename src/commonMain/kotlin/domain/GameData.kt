package domain

import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korma.geom.Point
import domain.event.EventCondition
import domain.event.GameEvent
import domain.port.market.CargoItem
import domain.port.market.Market
import domain.port.shipyard.ShipBlueprint
import domain.port.shipyard.Shipyard

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
    var location: Point,    // 현재 위치.
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
    val sprite: Bitmap get() = GameData.getBlueprint(type).imgSprite
}

class Product(
    val id: ProductId,
    val type: ProductType,
    val name: String,
    val price: Int,
)

class Site(
    val id: SiteId,
    val name: String,
    val subtitle: String,
)