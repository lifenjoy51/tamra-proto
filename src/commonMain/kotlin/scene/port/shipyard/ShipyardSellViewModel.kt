package scene.port.shipyard

import domain.Fleet
import domain.GameStore
import domain.Ship
import domain.port.shipyard.Shipyard
import scene.port.PortScene
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