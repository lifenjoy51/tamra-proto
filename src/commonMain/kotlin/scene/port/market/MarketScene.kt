package scene.port.market

import ViewModelProvider
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.fixedSizeContainer
import com.soywiz.korge.view.positionY
import com.soywiz.korge.view.solidRect
import com.soywiz.korim.color.Colors
import defaultMargin
import domain.GameStore
import mainHeight
import mainWidth
import scene.port.PortScene
import ui.tamraButton
import ui.tamraText

class MarketScene(private val store: GameStore, viewModelProvider: ViewModelProvider) : Scene() {

    private val vm = viewModelProvider.marketViewModel
    private val buyView = MarketBuyView(vm) { sceneContainer.changeTo<PortScene>() }
    private val sellView = MarketSellView(vm) { sceneContainer.changeTo<PortScene>() }

    override suspend fun Container.sceneInit() {
        val background = solidRect(width = mainWidth, height = mainHeight)
        // draw ui..
        val buyArea = fixedSizeContainer(mainWidth, mainHeight * 6 / 10) {
            positionY(mainHeight * 4 / 10)
        }
        buyView.draw(buyArea)

        tamraText(text = "", color = Colors.BLACK) {
            vm.money.observe { text = it.toString() }
        }

        tamraText("시장", color = Colors.BLACK, hc = background) {
            positionY(defaultMargin)
        }

        tamraButton(text = "X", textSize = 10.0, width = 20.0, height = 20.0, px = mainWidth - 25, py = defaultMargin / 2) {
            onClick { sceneContainer.changeTo<PortScene>() }
        }

        tamraButton(text = "구매", width = mainWidth / 2.0, px = 0, py = 30) {
            onClick {
                println("buy")
            }
        }

        tamraButton(text = "판매", width = mainWidth / 2.0, px = mainWidth / 2, py = 30) {
            onClick {
                println("sell")
            }
        }

        // init vm
        vm.init()
    }
}

class MarketSellView(val vm: MarketViewModel, val changePortScene: suspend () -> PortScene)
