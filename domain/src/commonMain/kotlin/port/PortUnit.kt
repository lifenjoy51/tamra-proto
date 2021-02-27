package tamra.port

import tamra.common.GameUnit
import tamra.common.LocationXY

data class Player(
    override var location: LocationXY,
    override var velocity: Double = 1.2,
    override val map: PortMap,
) : GameUnit() {
    fun moveUp() {
        moved(dy = -velocity)
    }

    fun moveDown() {
        moved(dy = +velocity)
    }

    fun moveLeft() {
        moved(dx = -velocity)
    }

    fun moveRight() {
        moved(dx = +velocity)
    }
}