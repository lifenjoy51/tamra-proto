package domain

import com.soywiz.kds.IntMap
import com.soywiz.korim.bitmap.Bitmap

class GameData(
    val ports: Map<PortId, Port>,
    val products: Map<ProductId, Product>,
    val shipBlueprints: Map<ShipType, ShipBlueprint>
) {
    companion object {
        lateinit var instance: GameData
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
    val price: Int
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
    val shipYard: ShipYard
)

class Product(
    val id: ProductId,
    val type: ProductType,
    val name: String,
    val price: Int,
)

class Market(
    val products: List<Product>
)

class ShipYard(
    val shipsOnSale: List<ShipBlueprint>
)
