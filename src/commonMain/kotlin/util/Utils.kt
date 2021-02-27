package util

import com.soywiz.korma.geom.Point
import tamra.common.LocationXY

fun LocationXY.toPoint() = Point(this.x, this.y)