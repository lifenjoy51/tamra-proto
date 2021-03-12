package scene.battle

import tamra.battle.AiBattleShip
import tamra.battle.BattleMap
import tamra.battle.BattleShip
import tamra.battle.PlayerBattleShip
import tamra.common.BattleSiteId
import tamra.common.GameStore
import tamra.common.TileXY
import ui.LiveData

class BattleViewModel(
    private val store: GameStore
) {
    lateinit var battleMap: BattleMap
    lateinit var player: PlayerBattleShip
    lateinit var ai: AiBattleShip
    var turn: LiveData<BattleShip> = LiveData(null)
    var action: LiveData<BattleShip> = LiveData(null)

    fun init(battleMap: BattleMap) {
        this.battleMap = battleMap
        player = initPlayer()
        ai = initAi()
        turn(player)
        onAction(ai)
        onAction(player)
    }

    private fun initPlayer(): PlayerBattleShip {
        val location = battleMap.sites.getValue(BattleSiteId.ALLY)
        val p = PlayerBattleShip(
            battleMap = battleMap,
            location = location
        )
        return p
    }

    private fun initAi(): AiBattleShip {
        val location = battleMap.sites.getValue(BattleSiteId.ENEMY)
        val a = AiBattleShip(
            battleMap = battleMap,
            location = location
        )
        return a
    }

    fun nextTurn() {
        val t = when (turn.get()) {
            player -> ai
            ai -> player
            else -> throw IllegalStateException()
        }
        // 턴이 넘어간 후에..
        t.newTurn()
        turn(t)
        onAction(t)
    }

    fun onAction(bs: BattleShip) {
        action(bs)
    }

    private suspend fun move(xy: TileXY, delay: suspend () -> Unit) {
        while (xy != player.tile) {
            player.move()
            onAction(player)
            delay()
        }
    }

    private fun turnClockwise() {
        val a = action.get()
        a.turnClockwise()
        onAction(a)
    }

    private fun turnCounterClockwise() {
        val a = action.get()
        a.turnCounterClockwise()
        onAction(a)
    }

    private fun meleeAttack() {
        val a = action.get()
        val e = ai
        a.meleeAttack(e)
        onAction(a)
    }

    private fun rangeAttack() {
        val a = action.get()
        val e = ai
        a.rangeAttack(e)
        onAction(a)
    }

    fun onClickTileXy(xy: TileXY): List<Action> {
        val list = mutableListOf<Action>()
        if (turn.get() is PlayerBattleShip) {
            if (xy in player.movableArea()) list.add(Action.MOVE)
            if (xy in player.clockwise()) list.add(Action.TURN_CLOCKWISE)
            if (xy in player.counterClockwise()) list.add(Action.TURN_COUNTER_CLOCKWISE)
            if (xy in player.meleeArea() && xy == ai.tile) list.add(Action.ATTACK_MELEE)
            if (xy in player.rangeArea() && xy == ai.tile) list.add(Action.ATTACK_RANGE)
        }
        return list
    }

    fun getEnemy(bs: BattleShip): BattleShip {
        return when (bs) {
            is PlayerBattleShip -> ai
            is AiBattleShip -> player
            else -> throw IllegalStateException()
        }
    }

    suspend fun doAction(action: Action, xy: TileXY, delay: suspend () -> Unit) {
        when (action) {
            Action.MOVE -> move(xy, delay)
            Action.TURN_CLOCKWISE -> turnClockwise()
            Action.TURN_COUNTER_CLOCKWISE -> turnCounterClockwise()
            Action.ATTACK_MELEE -> meleeAttack()
            Action.ATTACK_RANGE -> rangeAttack()
        }
    }
}

enum class Action(val desc: String) {
    MOVE("이동"),
    TURN_CLOCKWISE("우회전"),
    TURN_COUNTER_CLOCKWISE("좌회전"),
    ATTACK_MELEE("백병전"),
    ATTACK_RANGE("활쏘기")
}