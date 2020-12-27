package scene.port

import ViewModelProvider
import com.soywiz.korge.input.onClick
import com.soywiz.korge.tiled.TiledMap
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.view.*
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import domain.BuildingType
import domain.port.PortMap
import domain.port.PortTile
import mainHeight
import mainWidth
import ui.tamraButton
import util.getMovableArea
import util.getObjectNames
import util.loadTiles

class PortView(
    viewModelProvider: ViewModelProvider,
    private val changeWorldScene: suspend () -> Unit
) {
    private val vm = viewModelProvider.portViewModel

    suspend fun draw(container: Container) {
        val tiledMap = resourcesVfs["port.tmx"].readTiledMap()

        val tileTypes = getTileTypes(tiledMap)
        val terrains = tiledMap.loadTiles("terrain", tileTypes)
        val sites = tiledMap.loadTiles("site", tileTypes)
        val movableArea = tiledMap.getMovableArea()
        val buildings = tiledMap.getObjectNames("buildings").mapValues {
            BuildingType.valueOf(it.value)
        }

        val portMap = PortMap(movableArea, tiledMap.tileheight, buildings)

        //
        container.apply {
            val viewPlayer = sprite(resourcesVfs["player.png"].readBitmap())
            tiledMapView(tiledMap) {
                addChild(viewPlayer)
            }.apply {
                scale = 6.0
            }

            // on update player position
            vm.player.observe {
                viewPlayer.x = it.xy.x - viewPlayer.width / 2
                viewPlayer.y = it.xy.y - viewPlayer.height / 2
            }

            val background = SolidRect(width = mainWidth, height = mainHeight)

            tamraButton(
                text = "바다로 나가기",
                width = 140.0
            ).apply {
                alignX(background, 0.98, true)
                alignY(background, 0.99, true)
                onClick {
                    vm.leavePort()
                    changeWorldScene()
                }
                vm.currentBuilding.observe {
                    visible = (it == BuildingType.DOCK.name)
                }
            }

            tamraButton(
                text = "배만드는곳",
                width = 120.0
            ).apply {
                alignX(background, 0.98, true)
                alignY(background, 0.99, true)
                onClick {
                    vm.enterShipyard()
                }
                vm.currentBuilding.observe {
                    visible = (it == BuildingType.SHIPYARD.name)
                }
            }

            tamraButton(
                text = "시장",
                width = 80.0
            ).apply {
                alignX(background, 0.98, true)
                alignY(background, 0.99, true)
                onClick {
                    vm.enterMarket()
                }
                vm.currentBuilding.observe {
                    visible = (it == BuildingType.MARKET.name)
                }
            }
        }

        // init vm
        vm.initPlayer(portMap)
        vm.initPort()
    }

    private fun getTileTypes(tiledMap: TiledMap): Map<Int, PortTile> {
        val tileSet = tiledMap.tilesets.first().data    // 타일셋이 하나일 때.
        return tileSet.tiles.associate { (id, type) ->
            // 0은 없음을 나타냄. 그러므로 맵 데이터는 각 id+1.
            (id + 1) to PortTile.fromType(type)
        } + mapOf(0 to PortTile.EMPTY)
    }
}