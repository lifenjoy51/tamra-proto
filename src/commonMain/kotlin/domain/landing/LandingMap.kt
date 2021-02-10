package domain.landing

import domain.GameMap
import domain.SiteId
import domain.TXY
import domain.XY

class LandingMap(
    override val movableArea: List<List<Pair<XY, XY>>>,
    val tileSize: Int,
    val sites: Map<TXY, SiteId>
) : GameMap()