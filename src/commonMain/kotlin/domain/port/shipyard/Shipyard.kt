package domain.port.shipyard

import com.soywiz.korim.bitmap.Bitmap
import domain.*

class ShipBlueprint(
    val type: ShipType,
    val typeName: String,
    val imgSprite: Bitmap,
    val cargoSize: Int,
    val speed: Int,
    val price: Int,
) {
    fun build(name: String): Ship {
        return Ship(
            type = type,
            cargoSize = cargoSize,
            speed = speed.toDouble(),
            name = name
        )
    }
}

const val maxShipSpace = 5

class Shipyard(
    val portId: PortId,
    val sellingShips: List<ShipBlueprint>
) {

    fun buy(fleet: Fleet, blueprint: ShipBlueprint) {
        if (fleet.ships.size >= maxShipSpace) return
        fleet.ships.add(blueprint.build(blueprint.typeName))
        fleet.balance -= blueprint.price
    }

    fun sell(fleet: Fleet, ship: Ship) {
        // 판매하려는 배가 없어졌을 때 화물이 넘치는지 확인.
        if (fleet.availableCargoSpace < ship.cargoSize) return
        fleet.ships.remove(ship)
        val sellingPrice = GameData.getBlueprint(ship.type).price / 4
        fleet.balance += sellingPrice
    }
}