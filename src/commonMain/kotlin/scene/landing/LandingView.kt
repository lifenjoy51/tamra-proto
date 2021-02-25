package scene.landing

import ViewModelProvider
import com.soywiz.korge.input.onClick
import com.soywiz.korge.tiled.TiledMap
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.SolidRect
import com.soywiz.korge.view.camera
import com.soywiz.korge.view.sprite
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import domain.SiteId
import mainHeight
import mainWidth
import scene.common.HeaderView
import ui.tamraButton

class LandingView(
    viewModelProvider: ViewModelProvider,
    private val changeWorldScene: suspend () -> Unit
) {
    private val vm = viewModelProvider.landingViewModel
    private val headerView = HeaderView(viewModelProvider)
    private val landingViewScale = 6.0

    suspend fun draw(container: Container, tiledMap: TiledMap) {

        //
        container.apply {
            val viewPlayer = sprite(resourcesVfs["player.png"].readBitmap())
            val camera = camera {
                tiledMapView(tiledMap) {
                    addChild(viewPlayer)
                    scale = landingViewScale
                }
            }

            // on update player position
            vm.player.observe {
                viewPlayer.x = it.location.x - viewPlayer.width / 2
                viewPlayer.y = it.location.y - viewPlayer.height / 2
                // centering camera
                camera.x = (camera.containerRoot.width / 2) - (it.location.x * landingViewScale)
                camera.y = (camera.containerRoot.height / 2) - (it.location.y * landingViewScale)
            }

            val background = SolidRect(width = mainWidth, height = mainHeight)

            // draw header
            headerView.draw(container)

            tamraButton(text = "바다로 나가기", width = 120.0, px = mainWidth - 130, py = mainHeight - 40) {
                onClick {
                    vm.leaveLanding()
                    changeWorldScene()
                }
                vm.currentSite.observe {
                    visible = (it == SiteId.EXIT.name)
                }
            }

            tamraButton(text = "발견", width = 120.0, px = mainWidth - 130, py = mainHeight - 40) {
                onClick {
                    //vm.leaveLanding()
                    //changeWorldScene()
                }
                vm.currentSite.observe {
                    visible = (it.isNotEmpty() && it != SiteId.EXIT.name)
                }
            }

        }

    }
}