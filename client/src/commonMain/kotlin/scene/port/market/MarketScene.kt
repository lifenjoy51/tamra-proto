package scene.port.market

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

class MarketScene(viewModelProvider: ViewModelProvider) : Scene() {

    private val buyVm = viewModelProvider.marketBuyViewModel
    private val buyView = MarketBuyView(buyVm) { sceneContainer.changeTo<PortScene>() }

    private val sellVm = viewModelProvider.marketSellViewModel
    private val sellView = MarketSellView(sellVm) { sceneContainer.changeTo<PortScene>() }

    override suspend fun Container.sceneInit() {

        // draw ui..
        val background = solidRect(width = mainWidth, height = mainHeight)

        val area = fixedSizeContainer(mainWidth, mainHeight * 6 / 10) {
            positionY(mainHeight * 4 / 10)
        }

        tamraText("시장", color = Colors.BLACK)

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