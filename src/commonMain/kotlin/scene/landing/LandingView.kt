package scene.landing

import com.soywiz.klock.TimeSpan
import com.soywiz.korge.input.onClick
import com.soywiz.korge.tiled.TiledMap
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.view.*
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import scene.common.HeaderView
import tamra.ViewModelProvider
import tamra.common.Direction
import tamra.common.SiteId
import tamra.mainHeight
import tamra.mainWidth
import ui.getDirectionSprites
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

            val playerSprites = resourcesVfs["player.png"].readBitmap()
                .getDirectionSprites()
            val viewPlayer = sprite(playerSprites.getValue(Direction.DOWN)) {
                center()
            }

            val camera = camera {
                tiledMapView(tiledMap) {
                    addChild(viewPlayer)
                    scale = landingViewScale
                }
            }

            // on update player position
            vm.player.observe {
                viewPlayer.x = it.location.x
                viewPlayer.y = it.location.y
                // centering camera
                camera.x = (camera.containerRoot.width / 2) - (it.location.x * landingViewScale)
                camera.y = (camera.containerRoot.height / 2) - (it.location.y * landingViewScale)
            }
            vm.playerDirection.observe {
                val sprite = playerSprites.getValue(it)
                viewPlayer.playAnimation(sprite, spriteDisplayTime = TimeSpan(200.0))
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