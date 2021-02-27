package scene.common

import tamra.common.GameStore
import ui.LiveData

class HeaderViewModel(private val store: GameStore) {

    val balance: LiveData<Int> = LiveData(null)
    val menu: LiveData<String> = LiveData(null)

    fun clear() {
        balance.clear()
        menu.clear()
    }

    fun init() {
        balance(store.fleet.balance)
        menu(store.port()?.name ?: "")
    }

}