package domain

abstract class GameUnit {
    abstract var point: LocationXY
    abstract var v: Double
    abstract var size: LocationXY
}