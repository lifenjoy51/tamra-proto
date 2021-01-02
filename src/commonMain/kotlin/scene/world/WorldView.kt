package scene.world

import ViewModelProvider
import com.soywiz.korge.input.onClick
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.view.*
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import defaultMargin
import domain.GameData
import domain.PortId
import domain.world.WorldMap
import mainHeight
import mainWidth
import scene.common.FleetInfoView
import ui.tamraButton
import ui.tamraText
import util.getMovableArea
import util.getObjectNames
import windowHeight
import windowWidth

const val viewScale = 4.0

class WorldView(
    viewModelProvider: ViewModelProvider,
    private val changePortScene: suspend () -> Unit
) {
    private val vm: WorldViewModel = viewModelProvider.worldViewModel
    private val headerViewModel = viewModelProvider.headerViewModel
    private val fleetInfoVm = viewModelProvider.fleetInfoViewModel
    private val fleetInfoView = FleetInfoView(fleetInfoVm)

    suspend fun draw(container: Container) {
        val tiledMap = resourcesVfs["world.tmx"].readTiledMap()
        /*
        val tileSize = tiledMap.tilewidth
        val tileSet = tiledMap.tilesets.first().data    // 타일셋이 하나일 때.
        val tileTypeMap = tileSet.tiles.associate { (id, type) ->
            // 0은 없음을 나타냄. 그러므로 맵 데이터는 각 id+1.
            (id + 1) to WorldTile.fromType(type)
        } + mapOf(0 to WorldTile.EMPTY)

        val terrains = tiledMap.loadTiles("terrain", tileTypeMap)
        val sites = tiledMap.loadTiles("site", tileTypeMap)
        */
        val portPositions = tiledMap.getObjectNames("ports").mapValues { (k, v) ->
            GameData.ports[PortId.valueOf(v)]
        }
        val worldMap = WorldMap(tiledMap.getMovableArea(), tiledMap.tileheight, portPositions)

        container.apply {
            // TODO change texture..
            val viewFleet = sprite(texture = resourcesVfs["S100.png"].readBitmap())
            val camera = camera {
                tiledMapView(tiledMap) {
                    addChild(viewFleet)
                }
                scale = viewScale
            }

            // on update fleet position
            vm.playerFleet.observe {
                // move fleet view
                viewFleet.x = it.xy.x - viewFleet.width / 2
                viewFleet.y = it.xy.y - viewFleet.height / 2
                // centering camera
                camera.x = (camera.containerRoot.width / 2) - (it.xy.x * viewScale)
                camera.y = (camera.containerRoot.height / 2) - (it.xy.y * viewScale)
            }

            val background = SolidRect(width = mainWidth, height = mainHeight)

            // draw layer Fleet info
            val layerFleetInfo = fixedSizeContainer(windowWidth, windowHeight)
            fleetInfoView.draw(layerFleetInfo)
            fleetInfoVm.toggleFleetInfo.observe {
                layerFleetInfo.visible = it
            }

            // balance
            tamraText(text = "", textSize = 20.0) {
                headerViewModel.balance.observe { text = it.toString() }
            }

            tamraButton(text = "항구 들어가기", width = 140.0, px = mainWidth - 150, py = mainHeight - 50) {
                onClick {
                    if (!vm.nearPort.value.isNullOrEmpty()) {
                        vm.enterPort()
                        changePortScene.invoke()
                    }
                }
                vm.nearPort.observe { visible = it.isNotEmpty() }
            }

            tamraButton(width = 60.0, height = 40.0, textSize = 20.0, text = "정보", px = mainWidth - 60 - defaultMargin) {
                onClick { fleetInfoVm.toggleFleetInfo(true) }
            }
        }

        // init vm fleet
        vm.initPlayerFleet(worldMap)
        headerViewModel.init()
    }

}