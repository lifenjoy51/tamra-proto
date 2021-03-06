package scene.battle

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
import tamra.common.BattleSiteId
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

            }

            // on update position
            vm.player.observe {
                playerShipView.position(it)
                tempViews?.removeFromParent()
                tempViews = mapView.graphics { }
                drawMoveableGrid(it)
                drawDirection(it)
                mapView.sendChildToFront(playerShipView)
            }
            vm.ai.observe {
                aiShipView.position(it)
            }

            // draw header
            headerView.draw(container)

            //ui
            tamraText(text = "ALLY", px = 20, py = mainHeight - 160) {
                vm.turn.observe {
                    text = it.name
                }
            }
            tamraText(text = "", px = 20, py = mainHeight - 120) {
                vm.player.observe {
                    text = it.action.toString()
                }
            }

            tamraButton(text = "대기", width = 120.0, px = mainWidth - 130, py = mainHeight - 160) {
                onClick { vm.nextTurn() }
                vm.turn.observe {
                    //visible = it == BattleSiteId.ALLY
                }
            }

            tamraButton(text = "좌회전", width = 120.0, px = mainWidth - 130, py = mainHeight - 120) {
                onClick { vm.turnCounterClockwise() }
                vm.turn.observe {
                    visible = it == BattleSiteId.ALLY
                }
            }

            tamraButton(text = "우회전", width = 120.0, px = mainWidth - 130, py = mainHeight - 80) {
                onClick { vm.turnClockwise() }
                vm.turn.observe {
                    visible = it == BattleSiteId.ALLY
                }
            }

            tamraButton(text = "앞으로", width = 120.0, px = mainWidth - 130, py = mainHeight - 40) {
                onClick { vm.move() }
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
            stroke(Colors.SADDLEBROWN, StrokeInfo(thickness = 1.0)) {
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

    private fun Sprite.position(s: BattleShip) {
        this.x = s.location.x * tileSize - this.scaledWidth / 2 + tileSize / 2
        this.y = s.location.y * tileSize - this.scaledHeight / 2 + tileSize / 2
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