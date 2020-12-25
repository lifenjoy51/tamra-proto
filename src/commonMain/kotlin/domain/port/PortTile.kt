package domain.port

enum class PortTile(val type: Int) {
    EMPTY(0),
    WATER(1),
    LAND(2),
    DOCK(3),
    BUILDING(4);

    companion object {
        fun fromType(type: Int): PortTile {
            return when (type) {
                EMPTY.type -> EMPTY
                WATER.type -> WATER
                LAND.type -> LAND
                DOCK.type -> DOCK
                BUILDING.type -> BUILDING
                else -> throw Error("UNKNOWN")
            }
        }
    }
}