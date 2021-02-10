package util

import com.soywiz.korio.serialization.json.Json
import com.soywiz.korma.geom.Point
import domain.*
import domain.port.market.CargoItem
import domain.port.market.MarketProductState

data class PortProductId(
    val portId: PortId,
    val productId: ProductId,
)

class SaveManager {
    companion object {
        const val delimeter = "#"
        fun save(store: GameStore): String {
            val savedGame: MutableMap<String, Any> = mutableMapOf()
            savedGame["fleet.ships"] = store.fleet.ships.map {
                mapOf(
                    "type" to it.type.name,
                    "cargoSize" to it.cargoSize,
                    "speed" to it.speed,
                    "name" to it.name
                )
            }
            savedGame["fleet.cargoItems"] = store.fleet.cargoItems.map {
                mapOf("productId" to it.productId, "price" to it.price, "quantity" to it.quantity)
            }
            savedGame["fleet.balance"] = store.fleet.balance
            savedGame["fleet.port"] = store.fleet.port?.name ?: ""
            savedGame["fleet.location.x"] = store.fleet.location.x
            savedGame["fleet.location.y"] = store.fleet.location.y

            // FIXME 스토어에 저장되지 않고 상태값을 변경시키는 로직은 어디에 있어야 하는가?
            savedGame["marketStates.states"] = marketStates.map { (k, v) ->
                val portId = k.portId
                val productId = k.productId
                val marketSize = v.marketSize
                val marketStock = v.marketStock
                val supplyAndDemand = v.supplyAndDemand
                listOf(portId, productId, marketSize, marketStock, supplyAndDemand).joinToString(delimeter)
            }

            return Json.stringify(savedGame)
        }

        val marketStates: Map<PortProductId, MarketProductState>
            get() = GameData.ports.flatMap { (portId, port) ->
                port.market.marketProducts
                    .map { (productId, marketProduct) -> PortProductId(portId, productId) to marketProduct.state }
            }.toMap()

        fun load(savedGameJsonString: String): GameStore {
            val saved = Json.parse(savedGameJsonString) as Map<String, Any>
            val ships = (saved["fleet.ships"] as List<Map<String, Any>>).map {
                val type = it["type"]
                val cargoSize = it["cargoSize"]
                val speed = it["speed"]
                val name = it["name"]
                Ship(
                    ShipType.valueOf(type.toString()),
                    cargoSize.toString().toInt(),
                    speed.toString().toDouble(),
                    name.toString()
                )
            }

            val cargoItems = (saved["fleet.cargoItems"] as List<Map<String, Any>>).map { c ->
                CargoItem(
                    ProductId.valueOf(c["productId"].toString()),
                    c["price"].toString().toInt(),
                    c["quantity"].toString().toInt()
                )
            }.toMutableList()
            val balance = saved["fleet.balance"]
            val port = saved["fleet.port"]
            val location = Point(
                saved["fleet.location.x"].toString().toDouble(),
                saved["fleet.location.y"].toString().toDouble()
            )

            val marketProductStates: Map<PortProductId, MarketProductState> = (saved["marketStates.states"] as List<String>)
                .associate {
                    val v = it.split(delimeter)
                    val portId = PortId.valueOf(v[0])
                    val productId = ProductId.valueOf(v[1])
                    val marketSize = v[2].toInt()
                    val marketStock = v[3].toInt()
                    val supplyAndDemand = v[4].toInt()
                    PortProductId(portId, productId) to MarketProductState(
                        marketSize = marketSize, marketStock = marketStock, supplyAndDemand = supplyAndDemand
                    )
                }

            // FIXME 스토어에 저장되지 않고 상태값을 변경시키는 로직은 어디에 있어야 하는가?
            marketProductStates.map { (k, v) ->
                val market = GameData.ports.getValue(k.portId).market
                val marketProduct = market.marketProducts.getValue(k.productId)
                marketProduct.state.apply {
                    marketStock = v.marketStock
                    supplyAndDemand = v.supplyAndDemand
                }
            }

            return GameStore(
                fleet = Fleet(
                    ships = ships.toMutableList(),
                    balance = balance.toString().toInt(),
                    port = if (port == "") null else PortId.valueOf(port.toString()),
                    location = location,
                    cargoItems = cargoItems
                )
            )
        }
    }
}