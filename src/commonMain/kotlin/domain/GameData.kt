package domain

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
    val cargos: MutableList<ProductId>,
    val speed: Double,
    val name: String,
)

class ShipBlueprint(
    val type: ShipType,
    val typeName: String,
    val cargoSize: Int,
    val speed: Int,
    val price: Int
) {
    fun makeShip(name: String): Ship {
        return Ship(
            type = type,
            cargos = MutableList(cargoSize) { ProductId.EMPTY },
            speed = speed.toDouble(),
            name = name
        )
    }
}

class CargoSpace(
    val size: Int
)

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
