package scene.landing

import com.soywiz.klock.TimeSpan
import com.soywiz.korge.input.onClick
import com.soywiz.korge.tiled.TiledMap
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import scene.common.HeaderView
import tamra.ViewModelProvider
import tamra.common.Direction
import tamra.common.SiteId
import tamra.defaultMargin
import tamra.mainHeight
import tamra.mainWidth
import ui.getDirectionSprites
import ui.tamraButton
import ui.tamraRect

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
            tamraRect(width = mainWidth.toDouble(), height = mainHeight.toDouble(), color = Colors.DIMGREY)

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

            clipContainer(mainWidth, mainWidth) {
                positionY(32)
                addChild(camera)
            }

            // on update player position
            vm.player.observe {
                viewPlayer.x = it.location.x
                viewPlayer.y = it.location.y
                // centering camera
                camera.x = (mainWidth / 2) - (it.location.x * landingViewScale)
                camera.y = (mainWidth / 2) - (it.location.y * landingViewScale)
            }
            vm.playerDirection.observe {
                val sprite = playerSprites.getValue(it)
                viewPlayer.playAnimation(sprite, spriteDisplayTime = TimeSpan(200.0))
            }

            val background = SolidRect(width = mainWidth, height = mainHeight)

            // draw header
            headerView.draw(container)

            // move ui
            val btnSize = 50
            val btnSizeD = btnSize.toDouble()
            tamraButton(text = "△", width = btnSizeD, height = btnSizeD, ax = btnSize, ay = mainHeight - btnSize * 3 - defaultMargin * 2) {
                onClick { vm.up() }
            }
            tamraButton(text = "▽", width = btnSizeD, height = btnSizeD, ax = btnSize, ay = mainHeight - btnSize - defaultMargin * 2) {
                onClick { vm.down() }
            }
            tamraButton(text = "◁", width = btnSizeD, height = btnSizeD, ax = 0, ay = mainHeight - btnSize * 2 - defaultMargin * 2) {
                onClick { vm.left() }
            }
            tamraButton(text = "▷", width = btnSizeD, height = btnSizeD, ax = btnSize * 2, ay = mainHeight - btnSize * 2 - defaultMargin * 2) {
                onClick { vm.right() }
            }

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