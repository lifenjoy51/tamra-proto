package scene.event

import domain.GameData
import domain.GameStore
import domain.event.DecisionMaker
import domain.event.EventContent
import domain.event.EventLocation
import domain.event.GameEvent
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