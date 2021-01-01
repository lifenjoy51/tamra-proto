package scene

import domain.GameStore
import ui.LiveData

class HeaderViewModel(private val store: GameStore) {

    val money: LiveData<Int> = LiveData(null)
    val menu: LiveData<String> = LiveData(null)

    fun clear() {
        money.clear()
        menu.clear()
    }

    fun init() {
        money(store.money)
    }

}