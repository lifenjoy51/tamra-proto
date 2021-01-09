package domain.event

class GameEvent(
    val id: String,
    val location: EventLocation,
    val condition: EventCondition,
    val contents: List<EventContent>,
    var done: Boolean = false,
)

abstract class EventContent {
    abstract val eventId: String
    abstract val position: ContentPosition
    abstract val speaker: String
    abstract val lines: String
}

class Conversation(
    override val eventId: String,
    override val position: ContentPosition,
    override val speaker: String,
    override val lines: String,
) : EventContent()

class Narration(
    override val eventId: String,
    override val position: ContentPosition,
    override val speaker: String = "",
    override val lines: String,
) : EventContent()

// type@id#prop
// ============
// FLEET#BALANCE
// CARGO@SSAL#QUANTITY
// PORT
// PORT@JEJU
// PRODUCT@SSAL#PRICE
// VALUE@51
class Subject(
    val type: Type,
    val id: String? = null,
    val prop: Prop? = null
) {
    companion object {
        fun parse(s: String): Subject {
            val splitSharp = s.split("#")
            val prop = if (splitSharp.size == 2) Prop.valueOf(splitSharp[1]) else null
            val splitAt = splitSharp[0].split("@")
            val id = if (splitAt.size == 2) splitAt[1] else null
            val type = Type.valueOf(splitAt[0])
            return Subject(
                type = type, id = id, prop = prop
            )
        }
    }
}

class EventCondition(
    val id: String,
    val x: Subject,
    val op: Op,
    val y: Subject,
)
