package scene.battle

import com.soywiz.kmem.toIntCeil
import com.soywiz.korge.input.onClick
import com.soywiz.korge.tiled.TiledMap
import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.vector.line
import com.soywiz.korma.geom.vector.rect
import com.soywiz.korma.geom.vector.roundRect
import scene.common.HeaderView
import tamra.ViewModelProvider
import tamra.battle.BattleShip
import tamra.battle.MAX_ACTION
import tamra.common.BattleSiteId
import tamra.common.TileXY
import tamra.common.tileSize
import tamra.mainHeight
import tamra.mainWidth
import ui.tamraButton
import ui.tamraRect
import ui.tamraText

class BattleView(
    viewModelProvider: ViewModelProvider,
    private val changeWorldScene: suspend () -> Unit
) {
    private val vm = viewModelProvider.battleViewModel
    private val headerView = HeaderView(viewModelProvider)
    private val battleViewScale = 6.0
    var tempViews: Graphics? = null

    suspend fun draw(container: Container, tiledMap: TiledMap) {

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

            val mapView = tiledMapView(tiledMap) {
                positionY(32)
                scaledWidth = mainWidth.toDouble()
                scaledHeight = mainWidth.toDouble()
                addChild(playerShipView)
                addChild(aiShipView)

                //scale = battleViewScale
                drawGrid(this@tiledMapView)
                onClick {
                    val clickXy = (it.currentPosLocal / tileSize).let { p ->
                        TileXY(p.x.toIntCeil(), p.y.toIntCeil())
                    }
                }
            }

            fun drawTemp(bs: BattleShip) {
                tempViews?.removeFromParent()
                tempViews = mapView.graphics { }
                drawMoveableGrid(bs)
                drawRangeGrid(bs)
                drawMeleeGrid(bs)
                drawDirection(bs)
            }

            // on update position
            vm.player.observe {
                playerShipView.position(it)
                mapView.sendChildToFront(playerShipView)
                drawTemp(it)
            }
            vm.ai.observe {
                aiShipView.position(it)
                mapView.sendChildToFront(aiShipView)
                drawTemp(it)
            }

            // draw header
            headerView.draw(container)

            //ui
            tamraText(text = "", px = 15, py = mainHeight - 150) {
                vm.turn.observe {
                    val t = if (it == BattleSiteId.ALLY) "아군" else "적군"
                    text = "차례 : $t"
                }
            }
            tamraText(text = "", px = 15, py = mainHeight - 110) {
                vm.turn.observe {
                    val bs = it.getBattleShip(vm)
                    text = "행동 : ${bs.action}/$MAX_ACTION"
                }
            }
            tamraText(text = "", px = 15, py = mainHeight - 70) {
                vm.turn.observe {
                    val bs = it.getBattleShip(vm)
                    text = "선원 : ${bs.crew}/${bs.originCrew}"
                }
            }
            tamraText(text = "", px = 15, py = mainHeight - 30) {
                vm.turn.observe {
                    val bs = it.getBattleShip(vm)
                    text = "사기 : ${bs.morale}/${bs.originMorale}"
                }
            }

            tamraButton(text = "대기", width = 80.0, px = mainWidth - 90, py = mainHeight - 160) {
                onClick { vm.nextTurn() }
                vm.turn.observe {
                    visible = it == BattleSiteId.ALLY
                }
            }

            tamraButton(text = "좌회전", width = 80.0, px = mainWidth - 90, py = mainHeight - 120) {
                onClick { vm.turnCounterClockwise() }
                vm.turn.observe {
                    visible = it == BattleSiteId.ALLY
                }
            }

            tamraButton(text = "우회전", width = 80.0, px = mainWidth - 90, py = mainHeight - 80) {
                onClick { vm.turnClockwise() }
                vm.turn.observe {
                    visible = it == BattleSiteId.ALLY
                }
            }

            tamraButton(text = "앞으로", width = 80.0, px = mainWidth - 90, py = mainHeight - 40) {
                onClick { vm.move(vm.player.get()) }
                vm.turn.observe {
                    visible = it == BattleSiteId.ALLY
                }
            }

            // turn....
            vm.turn.observe {
                tempViews?.removeFromParent()
            }

        }

    }

    private fun drawDirection(it: BattleShip) {
        tempViews?.apply {
            stroke(Colors.WHITESMOKE, StrokeInfo(thickness = 2.0)) {
                val xy = it.forwardXy()
                roundRect(xy.x * tileSize + tileSize / 4, xy.y * tileSize + tileSize / 4, tileSize / 2, tileSize / 2, 2, 2)
            }
        }
    }

    private fun drawMoveableGrid(s: BattleShip) {
        tempViews?.apply {
            stroke(Colors.DARKGREEN, StrokeInfo(thickness = 1.0)) {
                s.movableArea().map { (x, y) ->
                    rect(x * tileSize, y * tileSize, tileSize, tileSize)
                }
            }
        }
    }

    private fun drawMeleeGrid(s: BattleShip) {
        tempViews?.apply {
            stroke(Colors.DARKBLUE, StrokeInfo(thickness = 1.0)) {
                s.meleeArea().map { (x, y) ->
                    rect(x * tileSize + 1, y * tileSize + 1, tileSize - 2, tileSize - 2)
                }
            }
        }
    }

    private fun drawRangeGrid(s: BattleShip) {
        tempViews?.apply {
            stroke(Colors.DARKRED, StrokeInfo(thickness = 1.0)) {
                s.rangeArea().map { (x, y) ->
                    rect(x * tileSize + 2, y * tileSize + 2, tileSize - 4, tileSize - 4)
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
                stroke(Colors.BLACK, StrokeInfo(thickness = 0.1)) {
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