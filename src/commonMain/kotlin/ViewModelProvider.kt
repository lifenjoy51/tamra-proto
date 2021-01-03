import scene.common.FleetInfoViewModel
import scene.common.HeaderViewModel
import scene.port.PortViewModel
import scene.port.market.MarketBuyViewModel
import scene.port.market.MarketSellViewModel
import scene.port.shipyard.ShipyardBuyViewModel
import scene.port.shipyard.ShipyardSellViewModel
import scene.world.WorldViewModel

class ViewModelProvider(
    val headerViewModel: HeaderViewModel,
    val worldViewModel: WorldViewModel,
    val fleetInfoViewModel: FleetInfoViewModel,
    val portViewModel: PortViewModel,
    val marketBuyViewModel: MarketBuyViewModel,
    val marketSellViewModel: MarketSellViewModel,
    val shipyardBuyViewModel: ShipyardBuyViewModel,
    val shipyardSellViewModel: ShipyardSellViewModel,
)