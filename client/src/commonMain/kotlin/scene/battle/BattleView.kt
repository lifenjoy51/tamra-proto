package scene.battle

import com.soywiz.klock.TimeSpan
import com.soywiz.kmem.toIntFloor
import com.soywiz.korge.input.onClick
import com.soywiz.korge.tiled.TiledMap
import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.time.delay
import com.soywiz.korge.ui.uiProgressBar
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.line
import com.soywiz.korma.geom.vector.rect
import com.soywiz.korma.geom.vector.roundRect
import scene.common.HeaderView
import tamra.ViewModelProvider
import tamra.battle.BattleShip
import tamra.battle.MAX_ACTION
import tamra.battle.PlayerBattleShip
import tamra.common.TileXY
import tamra.common.tileSize
import tamra.defaultMargin
import tamra.mainHeight
import tamra.mainWidth
import ui.TamraUISkin
import ui.tamraButton
import ui.tamraRect
import ui.tamraText
import kotlin.coroutines.CoroutineContext

class BattleView(
    viewModelProvider: ViewModelProvider,
    private val changeWorldScene: suspend () -> Unit
) {
    private val vm = viewModelProvider.battleViewModel
    private val headerView = HeaderView(viewModelProvider)
    private var actionGridView: Graphics? = null
    private var actionListView: Container? = null


    suspend fun draw(container: Container, tiledMap: TiledMap, coroutineContext: CoroutineContext) {
        //
        container.apply {
            tamraRect(width = mainWidth.toDouble(), height = mainHeight.toDouble(), color = Colors.DIMGREY)

            val playerShipSprites = resourcesVfs["L200.png"].readBitmap()
            val playerShipView = sprite(playerShipSprites) {
                scale = 0.1
            }

            val aiShipSprites = resourcesVfs["M200.png"].readBitmap()
            val aiShipView = sprite(aiShipSprites) {
                scale = 0.1
            }

            fun Point.getTileXy() = (this / tileSize).let { p ->
                TileXY(p.x.toIntFloor(), p.y.toIntFloor())
            }

            fun TiledMapView.actionListView(pos: Point, list: List<Action>) = container {
                position(pos)
                tamraRect(16.0, list.size * 6.0, color = Colors.BLACK)
                list.forEachIndexed { i, action ->
                    tamraText(action.desc, textSize = 4.0, px = 1, py = 1 + i * 6) {
                        onClick {
                            vm.doAction(action, pos.getTileXy()) {
                                this@actionListView.delay(TimeSpan(500.0))
                            }
                        }
                    }
                }
            }

            val mapView = tiledMapView(tiledMap) {
                positionY(32)
                scaledWidth = mainWidth.toDouble()
                scaledHeight = mainWidth.toDouble()
                addChild(playerShipView)
                addChild(aiShipView)

                //scale = battleViewScale
                drawGrid(this@tiledMapView)
                onClick { e ->
                    val clickXy = e.currentPosLocal.getTileXy()
                    vm.onClickTileXy(clickXy).let { list ->
                        actionListView?.removeFromParent()
                        if (list.isNotEmpty()) {
                            actionListView = actionListView(e.currentPosLocal, list)
                        }
                    }
                }
            }

            // on update position
            vm.turn.observe {
                actionGridView?.removeFromParent()
            }
            vm.action.observe { bs ->
                actionListView?.removeFromParent()
                if (bs is PlayerBattleShip) {
                    playerShipView.position(bs)
                } else {
                    aiShipView.position(bs)

                }
                actionGridView?.removeFromParent()
                actionGridView = mapView.graphics { }
                drawMoveableGrid(bs)
                drawRangeGrid(bs)
                drawMeleeGrid(bs)
                drawDirection(bs)
            }

            //ui
            val barWidth = mainWidth * 2.0 / 5.0
            val barHeight = 20.0
            val barX = 50.0
            tamraText("행동", ay = (mainHeight - defaultMargin - barHeight * 3 - 6).toInt())
            tamraText("선원", ay = (mainHeight - defaultMargin - barHeight * 2 - 6).toInt())
            tamraText("사기", ay = (mainHeight - defaultMargin - barHeight * 1 - 6).toInt())
            uiProgressBar(width = barWidth, height = barHeight, maximum = 1.0, skin = TamraUISkin) {
                position(barX, mainHeight - defaultMargin - barHeight * 3)
                vm.action.observe { bs ->
                    current = bs.action / MAX_ACTION.toDouble()
                    println(current)
                }
            }
            uiProgressBar(width = barWidth, height = barHeight, maximum = 1.0, skin = TamraUISkin) {
                position(barX, mainHeight - defaultMargin - barHeight * 2)
                vm.action.observe { bs ->
                    current = bs.crew / bs.originCrew.toDouble()
                }
            }
            uiProgressBar(width = barWidth, height = barHeight, maximum = 1.0, skin = TamraUISkin) {
                position(barX, mainHeight - defaultMargin - barHeight * 1)
                vm.action.observe { bs ->
                    current = bs.morale / bs.originMorale
                }
            }
            tamraButton(text = "턴종료", width = 80.0, px = mainWidth - 90, py = mainHeight - 40) {
                onClick { vm.nextTurn() }
                vm.turn.observe {
                    visible = it is PlayerBattleShip
                }
            }

            // draw header
            headerView.draw(container)

        }

    }

    private fun drawDirection(it: BattleShip) {
        actionGridView?.apply {
            fun xy(x: Int): Int = x * tileSize + tileSize / 4 + 1
            val wh = tileSize / 3
            stroke(Colors.WHITESMOKE.withAd(0.8), StrokeInfo(thickness = 2.0)) {
                val fXy = it.forwardXy()
                roundRect(xy(fXy.x), xy(fXy.y), wh, wh, 2, 2)
                val cwXy = it.clockwiseXy()
                roundRect(xy(cwXy.x), xy(cwXy.y), wh, wh, 2, 2)
                val ccwXy = it.counterClockwiseXy()
                roundRect(xy(ccwXy.x), xy(ccwXy.y), wh, wh, 2, 2)
            }
        }
    }

    private fun drawMoveableGrid(s: BattleShip) {
        actionGridView?.apply {
            stroke(Colors.DARKGREEN.withAd(0.5), StrokeInfo(thickness = 0.5)) {
                s.movableArea().map { (x, y) ->
                    rect(x * tileSize, y * tileSize, tileSize, tileSize)
                }
            }
        }
    }

    private fun drawMeleeGrid(s: BattleShip) {
        actionGridView?.apply {
            stroke(Colors.DARKORANGE.withAd(0.5), StrokeInfo(thickness = 0.5)) {
                s.meleeArea().map { (x, y) ->
                    rect(x * tileSize, y * tileSize, tileSize, tileSize)
                }
            }
        }
    }

    private fun drawRangeGrid(s: BattleShip) {
        actionGridView?.apply {
            stroke(Colors.DARKRED.withAd(0.5), StrokeInfo(thickness = 0.5)) {
                s.rangeArea().map { (x, y) ->
                    rect(x * tileSize + 1.5, y * tileSize + 1.5, tileSize - 2.0, tileSize - 2.0)
                }
            }
        }
    }

    private fun Sprite.position(s: BattleShip) {
        this.x = s.tile.x * tileSize - this.scaledWidth / 2 + tileSize / 2
        this.y = s.tile.y * tileSize - this.scaledHeight / 2 + tileSize / 2
    }

    private fun drawGrid(tiledMapView: TiledMapView) {
        tiledMapView.apply {
            graphics {
                stroke(Colors.DARKBLUE, StrokeInfo(thickness = 0.1)) {
                    for (w in 0..tiledMap.width) {
                        for (h in 0..tiledMap.height) {
                            line(w, h * tileSize, tiledMap.width * tileSize, h * tileSize)
                        }
                        line(w * tileSize, 0, w * tileSize, tiledMap.height * tileSize)
                    }
                }
            }
        }
    }
}