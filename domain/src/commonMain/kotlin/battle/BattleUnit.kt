package tamra.battle

import tamra.common.Direction
import tamra.common.TileXY

const val MAX_ACTION = 200
const val ACTION_PER_TURN = 100
const val MAX_ROTARY = 50
const val MAX_VELOCITY = 25

abstract class BattleShip(
    val map: BattleMap,
    var tile: TileXY,   // 위치
    var direction: Direction,   // 방향
    var action: Int = 100, // 행동력
    var velocity: Double = 1.0,  // 속도
    var rotary: Double = 1.0,   // 선회력
    var melee: Double = 0.2,  // 근접 공격
    var range: Double = 0.1,  // 원거리 공격
    var crew: Int = 20,  // 선원수
    var morale: Int = 100,    // 사기
) {
    val originCrew = crew
    val originMorale = morale

    abstract var auto: Boolean

    private val rotaryAction = (MAX_ROTARY / rotary).toInt()
    private val moveAction = (MAX_VELOCITY / velocity).toInt()

    private fun Direction.movableDistance(direction: Direction) = if (this == direction) action / moveAction else 0
    fun down() = Direction.DOWN.movableDistance(direction)
    fun up() = -Direction.UP.movableDistance(direction)
    fun right() = Direction.RIGHT.movableDistance(direction)
    fun left() = -Direction.LEFT.movableDistance(direction)

    fun newTurn() {
        action = (action + ACTION_PER_TURN).coerceAtMost(MAX_ACTION)
    }

    fun move() {
        val forwardXy = forwardXy()
        if (action - moveAction >= 0
            && (forwardXy.x != 0)
            && (forwardXy.y != 0)
            && (forwardXy.x != map.width - 1)
            && (forwardXy.y != map.height - 1)
        //&& forwardXy != enemy?.tile   // TODO check unit
        ) {
            tile = forwardXy
            action -= moveAction
        }
    }

    fun forwardXy(): TileXY {
        return when (this.direction) {
            Direction.DOWN -> tile.copy(y = tile.y + 1)
            Direction.UP -> tile.copy(y = tile.y - 1)
            Direction.RIGHT -> tile.copy(x = tile.x + 1)
            Direction.LEFT -> tile.copy(x = tile.x - 1)
        }
    }

    fun turnClockwise() {
        if (action - rotaryAction >= 0) {
            this.direction = when (this.direction) {
                Direction.RIGHT -> Direction.DOWN
                Direction.DOWN -> Direction.LEFT
                Direction.LEFT -> Direction.UP
                Direction.UP -> Direction.RIGHT
            }
            action -= rotaryAction
        }
    }

    fun turnCounterClockwise() {
        if (action - rotaryAction >= 0) {
            this.direction = when (this.direction) {
                Direction.RIGHT -> Direction.UP
                Direction.UP -> Direction.LEFT
                Direction.LEFT -> Direction.DOWN
                Direction.DOWN -> Direction.RIGHT
            }
            action -= rotaryAction
        }
    }

    private fun area(xl: Int, xr: Int, yu: Int, yd: Int): List<TileXY> {
        return (tile.x + xl..tile.x + xr).flatMap { w ->
            (tile.y + yu..tile.y + yd).map { h ->
                val x = w.coerceAtLeast(1)
                    .coerceAtMost(map.width - 2)
                val y = h.coerceAtLeast(1)
                    .coerceAtMost(map.height - 2)
                TileXY(x, y)
            }
        }
    }

    private fun tile(ax: Int = 0, ay: Int = 0): TileXY {
        val x = (tile.x + ax)
        val y = (tile.y + ay)
        return TileXY(x, y)
    }

    private fun List<TileXY>.clip(): List<TileXY> {
        return filter {
            it.x > 0 && it.y > 0 && it.x < map.width - 1 && it.y < map.height - 1
        }
    }

    fun movableArea(): List<TileXY> {
        return area(this.left(), this.right(), this.up(), this.down())
    }

    fun meleeArea(): List<TileXY> {
        // 상하좌우 1칸씩.
        return when (this.direction) {
            Direction.DOWN, Direction.UP -> {
                listOf(
                    tile(ax = -1),
                    tile(ax = +1),
                )
            }
            Direction.RIGHT, Direction.LEFT -> {
                listOf(
                    tile(ay = -1),
                    tile(ay = +1),
                )
            }
        }.clip()
    }

    fun rangeArea(): List<TileXY> {
        // 좌우 십자가칸 씩.
        return when (this.direction) {
            Direction.DOWN, Direction.UP -> {
                listOf(
                    tile(ax = -1), tile(ax = +1),
                    tile(ax = -2), tile(ax = +2),
                    tile(ax = -1, ay = -1), tile(ax = -1, ay = +1),
                    tile(ax = +1, ay = -1), tile(ax = +1, ay = +1),
                )
            }
            Direction.RIGHT, Direction.LEFT -> {
                listOf(
                    tile(ay = -1), tile(ay = +1),
                    tile(ay = -2), tile(ay = +2),
                    tile(ax = -1, ay = -1), tile(ax = -1, ay = +1),
                    tile(ax = +1, ay = -1), tile(ax = +1, ay = +1),
                )
            }
        }.clip()
    }
}

class PlayerBattleShip(
    battleMap: BattleMap,
    location: TileXY,
) : BattleShip(
    map = battleMap,
    tile = location,
    direction = Direction.RIGHT
) {
    override var auto = false
}

class AiBattleShip(
    battleMap: BattleMap,
    location: TileXY,
) : BattleShip(
    map = battleMap,
    tile = location,
    direction = Direction.LEFT
) {
    override var auto = true
}