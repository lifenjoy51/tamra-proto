package domain.landing

import com.soywiz.korma.geom.Point
import domain.GameMap
import domain.SiteId
import domain.TileXY

class LandingMap(
    override val movableArea: List<List<Pair<Point, Point>>>,
    val sites: Map<TileXY, SiteId>
) : GameMap()