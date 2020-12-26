package ui

class LiveData<T>(var value: T?) {
    private val callback: MutableList<(T) -> Unit> = mutableListOf()

    operator fun invoke(v: T, force: Boolean = false) {
        if (value != v || force) {
            value = v
            callback.forEach { it(value!!) }
        }
    }

    fun observe(block: (T) -> Unit) {
        callback.add(block)
    }
}