package util

import com.soywiz.korma.geom.Point
import domain.LocationXY

fun LocationXY.toPoint() = Point(this.x, this.y)