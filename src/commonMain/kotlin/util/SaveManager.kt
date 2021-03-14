package util

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tamra.common.GameStore

class SaveManager {
    companion object {
        fun save(store: GameStore): String {
            return Json.encodeToString(store)
        }

        fun load(savedGameJsonString: String): GameStore {
            return Json.decodeFromString(savedGameJsonString)
        }
    }
}