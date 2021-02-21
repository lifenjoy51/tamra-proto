package domain.landing

import domain.SiteId
import domain.TileXY

class LandingMap(
    val sites: Map<TileXY, SiteId>
)