package tamra.battle

import com.soywiz.korma.algo.AStar
import com.soywiz.korma.geom.fastForEach
import tamra.common.Direction
import tamra.common.TileXY

class BattleAI {

    suspend fun run(bs: BattleShip,
                    enemy: BattleShip,
                    delay: suspend () -> Unit,
                    onAction: suspend () -> Unit,
                    onEnd: suspend () -> Unit) {
        if (bs.auto) {
            var i = 0
            while (i++ < 5) {
                delay()
                bs.doAuto(enemy)
                onAction()
            }
            onEnd()
        }
    }

    private fun BattleShip.doAuto(enemy: BattleShip) {
        if (enemy == null) return
        val pTile = enemy.tile
        val board = com.soywiz.kds.Array2(map.width, map.height) { false } // 모두 장애물 아님.
        val points = AStar.find(
            board = board,
            x0 = tile.x,
            y0 = tile.y,
            x1 = pTile.x,
            y1 = pTile.y,
            findClosest = true
        )
        // TODO 이동하기.
        val route = points.let {
            val l = mutableListOf<TileXY>()
            val a = it.fastForEach { x, y ->
                l += TileXY(x, y)
            }
            l
        }

        if (route.size <= 1) {
            return
        }
        val t = route[1]
        val x = t.x
        val y = t.y

        if (t == tile) {
            return
        }

        when (this.direction) {
            Direction.DOWN -> when {
                forwardXy().y == y -> move()
                tile.x < x -> turnCounterClockwise()
                x < tile.x -> turnClockwise()
                else -> turnClockwise()
            }
            Direction.UP -> when {
                forwardXy().y == y -> move()
                tile.x < x -> turnClockwise()
                x < tile.x -> turnCounterClockwise()
                else -> turnClockwise()
            }
            Direction.RIGHT -> when {
                forwardXy().x == x -> move()
                tile.y < y -> turnClockwise()
                y < tile.y -> turnCounterClockwise()
                else -> turnClockwise()
            }
            Direction.LEFT -> when {
                forwardXy().x == x -> move()
                tile.y < y -> turnCounterClockwise()
                y < tile.y -> turnClockwise()
                else -> turnClockwise()
            }
        }
    }
}