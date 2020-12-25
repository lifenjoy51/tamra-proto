package domain

data class GameContext(
    val ships: MutableList<Ship>,
    var port: PortId?,
    var location: XY,
)