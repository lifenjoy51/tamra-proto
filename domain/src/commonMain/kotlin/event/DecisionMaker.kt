package tamra.event

import tamra.common.GameData
import tamra.common.GameStore

class DecisionMaker(
    val store: GameStore
) {

    private fun validate(c: EventCondition): Boolean = c.x.type.let {
        c.x.prop in it.props() && c.op in it.ops() && c.y.type in it.types()
    }

    fun decide(c: EventCondition): Boolean {
        if (!validate(c)) return false

        val x = c.x
        val y = c.y
        val op = c.op

        when (x.type) {
            Type.CONDITION -> {
                val conditionX = GameData.getCondition(x.id!!)
                val conditionY = GameData.getCondition(y.id!!)
                return when (op) {
                    Op.AND -> decide(conditionX) && decide(conditionY)
                    Op.OR -> decide(conditionX) || decide(conditionY)
                    else -> TODO()
                }
            }
            Type.FLEET -> {
                val fleet = store.fleet
                when (x.prop) {
                    Prop.BALANCE -> {
                        val valueY = y.id!!.toInt()
                        val balance = fleet.balance
                        return when (op) {
                            Op.EQ -> balance == valueY
                            Op.NE -> balance != valueY
                            Op.LT -> balance < valueY
                            Op.GT -> balance > valueY
                            else -> TODO()
                        }
                    }
                    else -> TODO()
                }
            }
            Type.CARGO -> {
                val cargos = store.fleet.cargoItems
                when (x.prop) {
                    Prop.QUANTITY -> {

                    }
                    else -> TODO()
                }
            }
            Type.PORT -> {
                val currentPort = store.port()!!
                val port = GameData.ports[y.id!!]!!
                return when (op) {
                    Op.EQ -> currentPort == port
                    Op.NE -> currentPort != port
                    else -> TODO()
                }
            }
            Type.PRODUCT -> {
                when (x.prop) {
                    Prop.PRICE -> {

                    }
                    else -> TODO()
                }
            }
            Type.VALUE -> TODO()
        }.run { Unit } // make when exhaustive
        return false
    }
}