package scene.battle

import tamra.battle.AiBattleShip
import tamra.battle.BattleMap
import tamra.battle.BattleShip
import tamra.battle.PlayerBattleShip
import tamra.common.BattleSiteId
import tamra.common.GameStore
import ui.LiveData

class BattleViewModel(
    private val store: GameStore
) {
    lateinit var battleMap: BattleMap
    val player: LiveData<PlayerBattleShip> = LiveData(null)
    val ai: LiveData<AiBattleShip> = LiveData(null)
    var turn: LiveData<BattleSiteId> = LiveData(null)

    fun init(battleMap: BattleMap) {
        this.battleMap = battleMap
        val p = initPlayer()
        val a = initAi()
        turn(BattleSiteId.ALLY)
        player(p as PlayerBattleShip)
    }

    private fun initPlayer(): BattleShip {
        val location = battleMap.sites.getValue(BattleSiteId.ALLY)
        val p = PlayerBattleShip(
            battleMap = battleMap,
            location = location
        )
        player(p)
        return p
    }

    private fun initAi(): BattleShip {
        val location = battleMap.sites.getValue(BattleSiteId.ENEMY)
        val a = AiBattleShip(
            battleMap = battleMap,
            location = location
        )
        ai(a)
        return a
    }

    fun move(bs: BattleShip) {
        bs.move()
        onAction(bs)
    }

    fun turnClockwise() {
        player.value?.let {
            it.turnClockwise()
            player(it)
        }
    }

    fun turnCounterClockwise() {
        player.value?.let {
            it.turnCounterClockwise()
            player(it)
        }
    }

    fun nextTurn() {
        val t = when (turn.get()) {
            BattleSiteId.ALLY -> BattleSiteId.ENEMY
            BattleSiteId.ENEMY -> BattleSiteId.ALLY
        }
        turn(t)
        // 턴이 넘어간 후에..
        when (t) {
            BattleSiteId.ALLY -> {
                player.get().let {
                    it.newTurn()
                    player(it)
                }

            }
            BattleSiteId.ENEMY -> {
                ai.get().let {
                    it.newTurn()
                    ai(it)
                }
            }
        }
    }

    fun onAction(bs: BattleShip) {
        when (bs) {
            is PlayerBattleShip -> player(bs)
            is AiBattleShip -> ai(bs)
        }
    }
}

fun BattleSiteId.getBattleShip(vm: BattleViewModel): BattleShip {
    return when (this) {
        BattleSiteId.ALLY -> vm.player.get()
        BattleSiteId.ENEMY -> vm.ai.get()
    }
}

fun BattleSiteId.getEnemy(vm: BattleViewModel): BattleShip {
    return when (this) {
        BattleSiteId.ALLY -> vm.ai.get()
        BattleSiteId.ENEMY -> vm.player.get()
    }
}