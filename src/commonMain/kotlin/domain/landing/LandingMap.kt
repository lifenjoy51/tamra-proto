package domain.landing

import com.soywiz.korma.geom.Point
import domain.GameMap
import domain.SiteId
import domain.TXY

class LandingMap(
    override val movableArea: List<List<Pair<Point, Point>>>,
    val tileSize: Int,
    val sites: Map<TXY, SiteId>
) : GameMap()