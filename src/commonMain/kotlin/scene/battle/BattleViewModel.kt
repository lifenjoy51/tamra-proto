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
    val player: LiveData<PlayerBattleShip> = LiveData(null)
    val ai: LiveData<AiBattleShip> = LiveData(null)
    var turn: LiveData<BattleSiteId> = LiveData(BattleSiteId.ALLY)

    fun init(battleMap: BattleMap) {
        this.battleMap = battleMap
        initPlayer()
        initAi()
    }

    private fun initPlayer() {
        val location = battleMap.sites.getValue(BattleSiteId.ALLY)
        onMovePlayer(PlayerBattleShip(
            battleMap = battleMap,
            location = location
        ))
    }

    private fun initAi() {
        val location = battleMap.sites.getValue(BattleSiteId.ENEMY)
        onMoveAi(AiBattleShip(
            battleMap = battleMap,
            location = location
        ))
    }

    private fun onMovePlayer(p: PlayerBattleShip) {
        player(p)
        scan(p)
    }

    private fun onMoveAi(a: AiBattleShip) {
        ai(a)
        scan(a)
    }

    private fun scan(ship: BattleShip) {
        val sites: Map<TileXY, BattleSiteId>
        // TODO
    }

    fun move() {
        player.value?.let {
            it.move()
            onMovePlayer(it)
        }
    }

    fun turnClockwise() {
        player.value?.let {
            it.turnClockwise()
            onMovePlayer(it)
        }
    }

    fun turnCounterClockwise() {
        player.value?.let {
            it.turnCounterClockwise()
            onMovePlayer(it)
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
                    onMovePlayer(it)
                }

            }
            BattleSiteId.ENEMY -> {
                ai.get().let {
                    it.newTurn()
                    onMoveAi(it)
                }
            }
        }
    }
}