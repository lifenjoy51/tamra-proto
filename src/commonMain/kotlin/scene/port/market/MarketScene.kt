package scene.port.market

import ViewModelProvider
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import defaultMargin
import mainHeight
import mainWidth
import scene.port.PortScene
import ui.tamraButton
import ui.tamraText

class MarketScene(viewModelProvider: ViewModelProvider) : Scene() {

    private val headerVm = viewModelProvider.headerViewModel

    private val buyVm = viewModelProvider.marketBuyViewModel
    private val buyView = MarketBuyView(buyVm) { sceneContainer.changeTo<PortScene>() }

    private val sellVm = viewModelProvider.marketSellViewModel
    private val sellView = MarketSellView(sellVm) { sceneContainer.changeTo<PortScene>() }

    override suspend fun Container.sceneInit() {
        // clear vm
        headerVm.clear()

        // draw ui..
        val background = solidRect(width = mainWidth, height = mainHeight)
        val area = fixedSizeContainer(mainWidth, mainHeight * 6 / 10) {
            positionY(mainHeight * 4 / 10)
        }

        tamraText(text = "", color = Colors.BLACK) {
            headerVm.money.observe { text = it.toString() }
        }

        tamraText("시장", color = Colors.BLACK, hc = background) {
            headerVm.menu.observe {
                text = "시장/$it"
                alignX(background, 0.5, true)
            }
        }

        tamraButton(text = "X", textSize = 10.0, width = 20.0, height = 20.0, px = mainWidth - 25, py = defaultMargin / 2) {
            onClick { sceneContainer.changeTo<PortScene>() }
        }

        tamraButton(text = "구매", width = mainWidth / 2.0, px = 0, py = 30) {
            onClick { initBuyArea(area) }
        }

        tamraButton(text = "판매", width = mainWidth / 2.0, px = mainWidth / 2, py = 30) {
            onClick { initSellArea(area) }
        }

        // init vm
        headerVm.init()
        initBuyArea(area)
    }

    private fun initBuyArea(area: FixedSizeContainer) {
        buyVm.clear()
        area.removeChildren()
        buyView.draw(area)
        buyVm.init()
        headerVm.menu("구매")
    }

    private fun initSellArea(area: FixedSizeContainer) {
        sellVm.clear()
        area.removeChildren()
        sellView.draw(area)
        sellVm.init()
        headerVm.menu("판매")
    }
}