package tamra.port

enum class PortTile(val type: Int) {
    EMPTY(0),
    WATER(1),
    LAND(2),
    BUILDING(3);

    companion object {
        fun fromType(type: Int): PortTile {
            return when (type) {
                EMPTY.type -> EMPTY
                WATER.type -> WATER
                LAND.type -> LAND
                BUILDING.type -> BUILDING
                else -> throw Error("UNKNOWN")
            }
        }
    }
}