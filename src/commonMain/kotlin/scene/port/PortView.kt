package scene.port

import com.soywiz.klock.TimeSpan
import com.soywiz.korge.input.onClick
import com.soywiz.korge.tiled.TiledMap
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.center
import com.soywiz.korge.view.positionY
import com.soywiz.korge.view.sprite
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import scene.common.HeaderView
import tamra.ViewModelProvider
import tamra.common.BuildingType
import tamra.common.Direction
import tamra.mainHeight
import tamra.mainWidth
import ui.getDirectionSprites
import ui.tamraButton
import ui.tamraRect

class PortView(
    viewModelProvider: ViewModelProvider,
    private val changeWorldScene: suspend () -> Unit,
    private val changeMarketScene: suspend () -> Unit,
    private val changeShipyardScene: suspend () -> Unit,
) {
    private val vm = viewModelProvider.portViewModel
    private val headerView = HeaderView(viewModelProvider)


    suspend fun draw(container: Container, tiledMap: TiledMap) {

        //
        container.apply {
            tamraRect(width = width, height = height, color = Colors["#e8f1f4"])

            val playerSprites = resourcesVfs["player.png"].readBitmap()
                .getDirectionSprites()
            val viewPlayer = sprite(playerSprites.getValue(Direction.DOWN)) {
                center()
            }

            tiledMapView(tiledMap) {
                positionY(32)
                addChild(viewPlayer)
                scaledWidth = mainWidth.toDouble()
                scaledHeight = mainWidth.toDouble()
            }

            // on update player position
            vm.player.observe {
                viewPlayer.x = it.location.x
                viewPlayer.y = it.location.y
            }
            vm.playerDirection.observe {
                val sprite = playerSprites.getValue(it)
                viewPlayer.playAnimation(sprite, spriteDisplayTime = TimeSpan(200.0))
            }

            // draw header
            headerView.draw(container)

            tamraButton(text = "바다로 나가기", width = 120.0, px = mainWidth - 130, py = mainHeight - 40) {
                onClick {
                    vm.leavePort()
                    changeWorldScene()
                }
                vm.currentBuilding.observe {
                    visible = (it == BuildingType.DOCK.name)
                }
            }

            tamraButton(text = "배만드는곳", width = 100.0, px = mainWidth - 110, py = mainHeight - 40) {
                onClick { changeShipyardScene() }
                vm.currentBuilding.observe {
                    visible = (it == BuildingType.SHIPYARD.name)
                }
            }

            tamraButton(text = "시장", width = 60.0, px = mainWidth - 70, py = mainHeight - 40) {
                onClick { changeMarketScene() }
                vm.currentBuilding.observe {
                    visible = (it == BuildingType.MARKET.name)
                }
            }
        }

    }
}