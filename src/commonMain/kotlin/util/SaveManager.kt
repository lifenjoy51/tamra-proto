package util

import com.soywiz.korio.serialization.json.Json
import domain.*

class SaveManager {
    companion object {
        const val delimeter = "#"
        fun save(store: GameStore): String {
            val savedGame: MutableMap<String, Any> = mutableMapOf()
            savedGame["ships"] = store.ships.map {
                mapOf(
                    "type" to it.type.name,
                    "cargos" to it.cargos.map { p -> mapOf("id" to p.id, "price" to p.price) },
                    "cargoSize" to it.cargoSize,
                    "speed" to it.speed,
                    "name" to it.name
                )
            }
            savedGame["money"] = store.money
            savedGame["port"] = store.port?.name ?: ""
            savedGame["location.x"] = store.location.x
            savedGame["location.y"] = store.location.y
            savedGame["marketStates"] = store.marketStates.map { (k, v) ->
                val portId = k.first
                val productId = k.second
                val marketSize = v.marketSize
                val marketStock = v.marketStock
                val marketPrice = v.marketPrice
                listOf(portId, productId, marketSize, marketStock, marketPrice).joinToString(delimeter)
            }

            return Json.stringify(savedGame)
        }

        fun load(savedGameJsonString: String): GameStore {
            val saved = Json.parse(savedGameJsonString) as Map<String, Any>
            val ships = (saved["ships"] as List<Map<String, Any>>).map {
                val type = it["type"]
                val cargos = (it["cargos"] as List<Map<String, Any>>).map { c ->
                    PurchasedProduct(
                        ProductId.valueOf(c["id"].toString()),
                        c["price"].toString().toInt()
                    )
                }
                val cargoSize = it["cargoSize"]
                val speed = it["speed"]
                val name = it["name"]
                Ship(
                    ShipType.valueOf(type.toString()),
                    cargos.toMutableList(),
                    cargoSize.toString().toInt(),
                    speed.toString().toDouble(),
                    name.toString()
                )
            }
            val money = saved["money"]
            val port = saved["port"]
            val location = XY(
                saved["location.x"].toString().toDouble(),
                saved["location.y"].toString().toDouble()
            )

            val marketStates: Map<Pair<PortId, ProductId>, MarketState> = (saved["marketStates"] as List<String>)
                .associate {
                    val v = it.split(delimeter)
                    val portId = PortId.valueOf(v[0])
                    val productId = ProductId.valueOf(v[1])
                    val marketSize = v[2].toInt()
                    val marketStock = v[3].toInt()
                    val marketPrice = v[4].toDouble()
                    (portId to productId) to MarketState(
                        marketSize = marketSize, marketStock = marketStock, marketPrice = marketPrice
                    )
                }

            return GameStore(
                ships.toMutableList(),
                money.toString().toInt(),
                if (port == "") null else PortId.valueOf(port.toString()),
                location,
                marketStates,
            )
        }
    }
}