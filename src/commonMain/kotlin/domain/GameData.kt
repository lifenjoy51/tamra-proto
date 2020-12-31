package domain

import com.soywiz.kds.IntMap
import com.soywiz.korim.bitmap.Bitmap

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
    }
}

class Ship(
    val type: ShipType,
    val cargos: IntMap<ProductId>,
    val speed: Double,
    val name: String,
)

class ShipBlueprint(
    val type: ShipType,
    val typeName: String,
    val imgSprite: Bitmap,
    val cargoSize: Int,
    val speed: Int,
    val price: Int,
) {
    fun makeShip(name: String): Ship {
        return Ship(
            type = type,
            cargos = IntMap(cargoSize, 1.0),
            speed = speed.toDouble(),
            name = name
        )
    }
}

class Port(
    val id: PortId,
    val name: String,
    val market: Market,
    val shipYard: ShipYard,
)

class Product(
    val id: ProductId,
    val type: ProductType,
    val name: String,
    val price: Int,
)

class Market(
    // 판매상품
    val saleProducts: List<Product>,
    // 시세.
    // val marketPrices: MutableMap<ProductId, Double>,
)

class ShipYard(
    val shipsOnSale: List<ShipBlueprint>,
)
