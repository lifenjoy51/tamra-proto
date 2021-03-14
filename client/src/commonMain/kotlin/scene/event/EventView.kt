package scene.event

import com.soywiz.korev.Key
import com.soywiz.korge.input.keys
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import tamra.ViewModelProvider
import tamra.defaultMargin
import tamra.event.Conversation
import tamra.event.Narration
import tamra.mainHeight
import ui.tamraRect
import ui.tamraText

class EventView(
    viewModelProvider: ViewModelProvider
) {
    private val vm = viewModelProvider.eventViewModel
    private val eventMargin = defaultMargin * 2.0

    suspend fun draw(container: Container) {
        container.apply {
            fixedSizeContainer(width, 150.0) {
                positionY(mainHeight - height)
                drawInternal(this)
                vm.toggle.observe { visible = it }

                keys.down {
                    when (it.key) {
                        Key.ENTER -> vm.next()
                    }
                }
            }
        }

        vm.init()
    }


    private suspend fun drawInternal(container: Container) {
        container.apply {
            visible = false

            tamraRect(width, height, color = Colors.BLACK)

            // conversation

            val speaker = tamraText("speaker", textSize = 24.0) {
                positionY(eventMargin)
                vm.content.observe {
                    visible = it is Conversation
                    text = it.speaker
                    alignX(container, it.position.x, true, eventMargin)
                }
            }
            tamraText("conversation-lines") {
                alignTopToBottomOf(speaker, eventMargin)
                vm.content.observe {
                    visible = it is Conversation
                    text = it.lines
                    alignX(container, 0.0, true, eventMargin)
                }
            }
            tamraText("narration-lines") {
                alignTopToBottomOf(speaker, defaultMargin)
                vm.content.observe {
                    visible = it is Narration
                    text = it.lines
                    alignX(container, it.position.x, true, eventMargin)
                }
            }

        }
    }
}