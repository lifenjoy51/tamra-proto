package scene.port.market

import ViewModelProvider
import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onOut
import com.soywiz.korge.input.onOver
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import domain.GameStore
import mainHeight
import mainWidth
import scene.port.PortScene
import ui.tamraButton
import ui.tamraText

class MarketScene(private val store: GameStore, viewModelProvider: ViewModelProvider) : Scene() {

    private val vm = viewModelProvider.marketViewModel
    private val buyView = MarketBuyView(vm)
    private val sellView = MarketSellView(vm)

    override suspend fun Container.sceneInit() {
        val background = solidRect(width = mainWidth, height = mainHeight)

        // draw ui..
        val defaultMargin = 5.0
        val buyArea = fixedSizeContainer(mainWidth, mainHeight * 6 / 10) {
            positionY(mainHeight * 4 / 10)
        }
        buyView.draw(buyArea)

        tamraText(
            text = "", textSize = 24.0, color = Colors.BLACK
        ).apply {
            alignX(background, 0.02, true)
            positionY(defaultMargin)
            vm.money.observe { text = it.toString() }
        }

        val marketTitle = tamraText("시장", color = Colors.BLACK, textSize = 24.0) {
            alignX(background, 0.5, true)
            positionY(defaultMargin)
        }

        tamraText("X", color = Colors.BLACK, textSize = 24.0) {
            alignX(background, 0.98, true)
            positionY(defaultMargin)
            alpha = 0.7
            onOut { this.alpha = 0.7 }
            onOver { this.alpha = 1.0 }
            onClick {
                sceneContainer.changeTo<PortScene>()
            }
        }

        tamraButton(
            text = "구매",
            width = mainWidth / 2.0
        ).apply {
            alignX(background, 0.0, true)
            positionY(marketTitle.height + defaultMargin * 2)
            onClick {
                println("buy")
            }
        }

        tamraButton(
            text = "판매",
            width = mainWidth / 2.0
        ).apply {
            alignX(background, 1.0, true)
            positionY(marketTitle.height + defaultMargin * 2)
            onClick {
                println("sell")

            }
        }

        // init vm
        vm.init()
        vm.changeToPort = {
            sceneContainer.changeTo<PortScene>()
        }
    }
}

class MarketSellView(val vm: MarketViewModel)
