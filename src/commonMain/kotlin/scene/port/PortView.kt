package scene.port

import ViewModelProvider
import com.soywiz.korge.input.onClick
import com.soywiz.korge.tiled.TiledMap
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.SolidRect
import com.soywiz.korge.view.sprite
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import domain.BuildingType
import mainHeight
import mainWidth
import scene.common.HeaderView
import ui.tamraButton

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
            val viewPlayer = sprite(resourcesVfs["player.png"].readBitmap())
            tiledMapView(tiledMap) {
                addChild(viewPlayer)
                scale = 6.0
            }

            // on update player position
            vm.player.observe {
                viewPlayer.x = it.xy.x - viewPlayer.width / 2
                viewPlayer.y = it.xy.y - viewPlayer.height / 2
            }

            val background = SolidRect(width = mainWidth, height = mainHeight)

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