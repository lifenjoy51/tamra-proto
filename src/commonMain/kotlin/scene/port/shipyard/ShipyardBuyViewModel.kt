package scene.port.shipyard

import domain.Fleet
import domain.GameStore
import domain.port.shipyard.ShipBlueprint
import domain.port.shipyard.Shipyard
import scene.port.PortScene
import ui.LiveData

class ShipyardBuyViewModel(private val store: GameStore) {
    private lateinit var shipyard: Shipyard

    val sellingShips: LiveData<List<ShipBlueprint>> = LiveData(null)
    val selectedBlueprint: LiveData<ShipBlueprint> = LiveData(null)
    val fleet: LiveData<Fleet> = LiveData(null)

    fun clear() {
        sellingShips.clear()
        selectedBlueprint.clear()
        fleet.clear()
    }

    fun init() {
        shipyard = store.port()!!.shipYard
        fleet(store.fleet)
        sellingShips(shipyard.sellingShips)
    }

    fun select(blueprint: ShipBlueprint) {
        selectedBlueprint(blueprint)
    }

    suspend fun buy(changePortScene: suspend () -> PortScene) {
        shipyard.buy(fleet.get(), selectedBlueprint.get())
        changePortScene()
    }
}