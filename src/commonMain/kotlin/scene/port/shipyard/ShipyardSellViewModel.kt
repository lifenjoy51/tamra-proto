package scene.port.shipyard

import scene.port.PortScene
import tamra.common.Fleet
import tamra.common.GameStore
import tamra.common.Ship
import tamra.port.shipyard.Shipyard
import ui.LiveData

class ShipyardSellViewModel(private val store: GameStore) {
    private lateinit var shipyard: Shipyard

    val selectedShip: LiveData<Ship> = LiveData(null)
    val fleet: LiveData<Fleet> = LiveData(null)

    fun clear() {
        selectedShip.clear()
        fleet.clear()
    }

    fun init() {
        shipyard = store.port()!!.shipYard
        fleet(store.fleet)
    }

    fun select(ship: Ship) {
        selectedShip(ship)
    }

    suspend fun sell(changePortScene: suspend () -> PortScene) {
        shipyard.sell(fleet.get(), selectedShip.get())
        changePortScene()
    }
}