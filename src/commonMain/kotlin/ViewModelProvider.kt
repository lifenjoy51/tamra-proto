import scene.HeaderViewModel
import scene.port.PortViewModel
import scene.port.market.MarketBuyViewModel
import scene.port.market.MarketSellViewModel
import scene.world.FleetInfoViewModel
import scene.world.WorldViewModel

class ViewModelProvider(
    val headerViewModel: HeaderViewModel,
    val worldViewModel: WorldViewModel,
    val fleetInfoViewModel: FleetInfoViewModel,
    val portViewModel: PortViewModel,
    val marketBuyViewModel: MarketBuyViewModel,
    val marketSellViewModel: MarketSellViewModel,
)