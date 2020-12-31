import scene.port.PortViewModel
import scene.port.market.MarketViewModel
import scene.world.FleetInfoViewModel
import scene.world.WorldViewModel

class ViewModelProvider(
    val worldViewModel: WorldViewModel,
    val fleetInfoViewModel: FleetInfoViewModel,
    val portViewModel: PortViewModel,
    val marketViewModel: MarketViewModel
)