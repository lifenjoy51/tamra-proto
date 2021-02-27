package tamra.world

enum class WorldTile(val type: Int) {
    EMPTY(0),
    WATER(1),
    LAND(2),
    PORT(3),
    RELIC(4);

    companion object {
        fun fromType(type: Int): WorldTile {
            return when (type) {
                EMPTY.type -> EMPTY
                WATER.type -> WATER
                LAND.type -> LAND
                PORT.type -> PORT
                RELIC.type -> RELIC
                else -> throw Error("UNKNOWN")
            }
        }
    }

}