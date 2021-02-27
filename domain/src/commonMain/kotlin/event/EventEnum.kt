package tamra.event

import kotlin.reflect.KClass


enum class EventLocation {
    PORT,
    WORLD,
    ALL
}

enum class ContentType(val kClass: KClass<out EventContent>) {
    N(Narration::class),  // Narration
    C(Conversation::class)   // Conversation
}

enum class ContentPosition(val x: Double) {
    C(0.5), // Center
    L(0.05),  // Left
    R(0.9)   // Right
}

enum class Type {
    CONDITION,  // 다른 조건들.
    FLEET,   // 플레이어의 함대.
    CARGO,   // 플레이어의 함대가 가지고 있는 화물.
    PORT,   // 정박중인 항구.
    PRODUCT,     // 정박중인 항구의 상품.
    VALUE,  // 비교대상으로 사용되는 값. 값의 형식은 맥락에 맞게 입력되어야 한다. 확인 불가.
}

enum class Prop {
    BALANCE,    // 잔고
    QUANTITY,   // 수량
    PRICE,   // 가격
}

enum class Op {
    EQ, // ==
    NE, // !=
    LT, // <
    GT, // >
    AND, // &&
    OR, // ||
}

/**
 * 이 타입과 연산이 가능한 상대 타입.
 */
fun Type.types(): List<Type> {
    return when (this) {
        Type.CONDITION -> listOf(Type.CONDITION)
        Type.FLEET -> listOf(Type.VALUE)
        Type.CARGO -> listOf(Type.VALUE)
        Type.PORT -> listOf(Type.PORT)
        Type.PRODUCT -> listOf(Type.VALUE)
        Type.VALUE -> listOf()
    }
}

/**
 * 이 타입이 가질 수 있는 속성.
 */
fun Type.props(): List<Prop> {
    return when (this) {
        Type.CONDITION -> listOf()
        Type.FLEET -> listOf(Prop.BALANCE)
        Type.CARGO -> listOf(Prop.QUANTITY)
        Type.PORT -> listOf()
        Type.PRODUCT -> listOf(Prop.PRICE)
        Type.VALUE -> listOf()
    }
}

/**
 * 이 타입과 연산이 가능한 연산자.
 */
fun Type.ops(): List<Op> {
    return when (this) {
        Type.CONDITION -> listOf(Op.AND, Op.OR)
        Type.FLEET -> listOf(Op.EQ, Op.NE, Op.LT, Op.GT)
        Type.CARGO -> listOf(Op.EQ, Op.NE, Op.LT, Op.GT)
        Type.PORT -> listOf(Op.EQ, Op.NE)
        Type.PRODUCT -> listOf(Op.EQ, Op.NE, Op.LT, Op.GT)
        Type.VALUE -> listOf()
    }
}