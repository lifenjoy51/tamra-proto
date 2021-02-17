package scene.common

import ViewModelProvider
import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import defaultMargin
import mainWidth
import ui.tamraButton
import ui.tamraText
import windowHeight
import windowWidth

class HeaderView(viewModelProvider: ViewModelProvider) {
    private val vm = viewModelProvider.headerViewModel
    private val fleetInfoVm = viewModelProvider.fleetInfoViewModel
    private val fleetInfoView = FleetInfoView(fleetInfoVm)

    fun draw(container: Container): FixedSizeContainer {
        return container.fixedSizeContainer(windowWidth, windowHeight) {
            // draw layer Fleet info
            val layerFleetInfo = fixedSizeContainer(windowWidth, windowHeight) {
                centerOnStage()
            }
            fleetInfoView.draw(layerFleetInfo)
            fleetInfoVm.toggleFleetInfo.observe {
                layerFleetInfo.visible = it
            }

            // balance
            tamraText(text = "") {
                vm.balance.observe { text = it.toString() }
            }

            // 현재 메뉴.
            tamraText("menu", hc = container) {
                vm.menu.observe {
                    text = it
                    alignX(container, 0.5, true)
                }
            }

            // 함대 정보.
            tamraButton(text = "정보", textSize = 14.0, width = 40.0, height = 24.0, px = mainWidth - 40 - defaultMargin / 2, py = defaultMargin / 2) {
                onClick { fleetInfoVm.toggleFleetInfo(true) }
            }

            vm.init()
        }
    }
}