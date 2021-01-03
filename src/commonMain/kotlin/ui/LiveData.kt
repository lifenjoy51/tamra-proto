package ui

class LiveData<T>(var value: T?) {
    private val callback: MutableList<(T) -> Unit> = mutableListOf()

    operator fun invoke(v: T) {
        value = v
        callback.forEach { it(value!!) }
    }

    fun observe(block: (T) -> Unit) {
        callback.add(block)
    }

    fun clear() {
        callback.removeAll { true }
    }

    fun get() = value!!
}