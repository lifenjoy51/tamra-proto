package scene.common

import domain.Fleet
import domain.GameStore
import domain.Ship
import ui.LiveData

class FleetInfoViewModel(
    private val store: GameStore
) {
    val toggleFleetInfo: LiveData<Boolean> = LiveData(false)
    val selectedShip: LiveData<Ship> = LiveData(null)
    val fleet: LiveData<Fleet> = LiveData(null)

    fun clear() {
        toggleFleetInfo.clear()
        selectedShip.clear()
        fleet.clear()
    }

    fun init() {
        fleet(store.fleet)
        selectShip(fleet.get().ships.first())
    }

    fun selectShip(ship: Ship) {
        selectedShip(ship)
    }
}