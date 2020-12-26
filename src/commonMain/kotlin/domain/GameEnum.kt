package domain

enum class ShipType {
    DOTBAE,
    CHOMASUN,
    GYOGWANSUN
}

enum class PortId {
    JEJU,
    MOSULPO,
    SUGUIPO,
    SUNGSANPO
}

enum class ProductType {
    FISH, // 어류
    GRAIN, // 곡물
    FOOD, // 식품(어류,곡물 제외)
    FABRIC,  // 옷감
    LUXURIES  // 사치품
}

enum class ProductId {
    GODUNGU,
    GALCHI,
    DOLDOM,

    SSAL,
    BORI,
    JO,

    SALT,
    PA,
    MANUL,
    BEEF,
    PORK,
    GYUL,

    MOSI,
    MOOMYUNG,
    MYUNGJOO,

    OMEGISUL,
    GOSORISUL,
    MALCHONG
}