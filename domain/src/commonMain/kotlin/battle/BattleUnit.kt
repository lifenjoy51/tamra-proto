package tamra.battle

import tamra.common.Direction
import tamra.common.TileXY

const val MAX_ACTION = 200
const val ACTION_PER_TURN = 100
const val MAX_ROTARY = 50
const val MAX_VELOCITY = 25

open class BattleShip(
    val map: BattleMap,
    var location: TileXY,   // 위치
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
        ) {
            location = forwardXy
            action -= moveAction
        }
    }

    fun forwardXy(): TileXY {
        return when (this.direction) {
            Direction.DOWN -> location.copy(y = location.y + 1)
            Direction.UP -> location.copy(y = location.y - 1)
            Direction.RIGHT -> location.copy(x = location.x + 1)
            Direction.LEFT -> location.copy(x = location.x - 1)
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

    fun movableArea(): List<TileXY> {
        val list = mutableListOf<TileXY>()
        for (w in this.left()..this.right()) {
            for (h in this.up()..this.down()) {
                val x = (w + location.x).coerceAtLeast(1).coerceAtMost(map.width - 2)
                val y = (h + location.y).coerceAtLeast(1).coerceAtMost(map.height - 2)
                list.add(TileXY(x, y))
            }
        }
        return list
    }
}

class PlayerBattleShip(
    battleMap: BattleMap,
    location: TileXY,
) : BattleShip(
    map = battleMap,
    location = location,
    direction = Direction.RIGHT
)

class AiBattleShip(
    battleMap: BattleMap,
    location: TileXY,
) : BattleShip(
    map = battleMap,
    location = location,
    direction = Direction.LEFT
)