package scene.event

import tamra.common.GameData
import tamra.common.GameStore
import tamra.event.DecisionMaker
import tamra.event.EventContent
import tamra.event.EventLocation
import tamra.event.GameEvent
import ui.LiveData

class EventViewModel(
    private val store: GameStore
) {
    private val decisionMaker = DecisionMaker(store)
    private var index = 0
    lateinit var event: GameEvent

    val toggle: LiveData<Boolean> = LiveData(false)
    val content: LiveData<EventContent> = LiveData(null)

    private fun checkEvents(): GameEvent? {
        return GameData.events.filterValues { it.location == EventLocation.PORT }
            .filterNot { it.key in store.doneEvents }
            .filterValues { decisionMaker.decide(it.condition) }
            .values.firstOrNull()
    }

    fun init() {
        checkEvents()?.let {
            this.event = it
            next()
            toggle(true)
        }
    }

    fun next() {
        if (index < event.contents.size) {
            content(event.contents[index++])
        } else {
            done()
        }
    }

    private fun done() {
        store.doneEvents.add(event.id)
        toggle(false)
    }


}