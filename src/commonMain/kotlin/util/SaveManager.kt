package util

import com.soywiz.kds.associateByInt
import com.soywiz.korio.serialization.json.Json
import domain.*

class SaveManager {
    companion object {

        fun save(context: GameContext): String {
            val savedGame: MutableMap<String, Any> = mutableMapOf()
            savedGame["ships"] = context.ships.map {
                mapOf(
                    "type" to it.type.name,
                    "cargos" to it.cargos.values.joinToString(),
                    "speed" to it.speed,
                    "name" to it.name
                )
            }
            savedGame["port"] = context.port?.name ?: ""
            savedGame["location.x"] = context.location.x
            savedGame["location.y"] = context.location.y

            return Json.stringify(savedGame)
        }

        fun load(savedGameJsonString: String): GameContext {
            val saved = Json.parse(savedGameJsonString) as Map<String, Any>
            val ships = (saved["ships"] as List<Map<String, Any>>).map {
                val type = it["type"]
                val cargos = it["cargos"]
                val speed = it["speed"]
                val name = it["name"]
                Ship(
                    ShipType.valueOf(type.toString()),
                    cargos.toString()
                        .split(",")
                        .map { p -> ProductId.valueOf(p.trim()) }
                        .associateByInt { index, _ -> index },
                    speed.toString().toDouble(),
                    name.toString()
                )
            }
            val port = saved["port"]
            val location = XY(
                saved["location.x"].toString().toDouble(),
                saved["location.y"].toString().toDouble()
            )
            return GameContext(
                ships.toMutableList(),
                if (port == "") null else PortId.valueOf(port.toString()),
                location
            )
        }
    }
}