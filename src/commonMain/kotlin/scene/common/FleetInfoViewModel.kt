package scene.common

import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.bitmap.Bitmap32
import domain.GameData
import domain.GameStore
import domain.Ship
import ui.LiveData

class FleetInfoViewModel(
    private val store: GameStore
) {
    val toggleFleetInfo: LiveData<Boolean> = LiveData(false)

    fun initPlayerShips() {
        playerShips(store.fleet.ships)
        selectShip(store.fleet.ships.first())
        shipCargos(store.fleet.cargoItems.map {
            val name = GameData.getProduct(it.productId).name
            val price = it.price
            val quantity = it.quantity
            "$name $price $quantity"
        }.joinToString("\n"))
    }

    fun selectShip(ship: Ship) {
        GameData.blueprints.getValue(ship.type).apply {
            shipImage(imgSprite)
            shipTypeName(typeName)
        }
        shipName(ship.name)
        shipSpeed(ship.speed.toString())
    }

    val playerShips: LiveData<MutableList<Ship>> = LiveData(mutableListOf())
    val shipImage: LiveData<Bitmap> = LiveData(Bitmap32(0, 0))
    val shipName: LiveData<String> = LiveData("")
    val shipSpeed: LiveData<String> = LiveData("")
    val shipTypeName: LiveData<String> = LiveData("")
    val shipCargos: LiveData<String> = LiveData("")
}