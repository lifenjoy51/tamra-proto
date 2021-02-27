package scene.port.shipyard

import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import scene.port.PortScene
import tamra.ViewModelProvider
import tamra.defaultMargin
import tamra.mainHeight
import tamra.mainWidth
import ui.tamraButton
import ui.tamraText

class ShipyardScene(viewModelProvider: ViewModelProvider) : Scene() {

    private val buyVm = viewModelProvider.shipyardBuyViewModel
    private val buyView = ShipyardBuyView(buyVm) { sceneContainer.changeTo<PortScene>() }

    private val sellVm = viewModelProvider.shipyardSellViewModel
    private val sellView = ShipyardSellView(sellVm) { sceneContainer.changeTo<PortScene>() }

    lateinit var area: FixedSizeContainer

    override suspend fun Container.sceneInit() {

        val background = solidRect(width = mainWidth, height = mainHeight)

        val header = fixedSizeContainer(mainWidth, 20 + defaultMargin * 2) {
            tamraText("배만드는곳", color = Colors.BLACK)

            tamraButton(text = "X", textSize = 10.0, width = 20.0, height = 20.0, px = mainWidth - 25, py = defaultMargin / 2) {
                onClick { sceneContainer.changeTo<PortScene>() }
            }
        }

        val tab = fixedSizeContainer(mainWidth, 30) {
            alignTopToBottomOf(header)
            tamraButton(text = "구입", width = mainWidth / 2.0, px = 0, py = 0) {
                onClick { initBuyArea(area) }
            }
            tamraButton(text = "매각", width = mainWidth / 2.0, px = mainWidth / 2, py = 0) {
                onClick { initSellArea(area) }
            }
        }

        area = fixedSizeContainer(mainWidth.toDouble(), mainHeight - (header.height + tab.height)) {
            alignTopToBottomOf(tab)
        }

        initBuyArea(area)
    }

    private fun initBuyArea(area: FixedSizeContainer) {
        buyVm.clear()
        area.removeChildren()
        buyView.draw(area)
        buyVm.init()
    }

    private fun initSellArea(area: FixedSizeContainer) {
        sellVm.clear()
        area.removeChildren()
        sellView.draw(area)
        sellVm.init()
    }
}