package scene.world

import com.soywiz.korge.input.onClick
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.view.*
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import domain.GameContext
import domain.GameData
import domain.PortId
import domain.world.PlayerFleet
import domain.world.WorldMap
import mainHeight
import mainWidth
import scene.PortScene
import ui.tamraButton
import util.getMovableArea
import util.getObjectNames
import windowHeight
import windowWidth

const val viewScale = 4.0

class WorldView(
        private val context: GameContext,
        private val vm: WorldViewModel,
        private val worldScene: WorldScene
) {
    private val fleetInfoView = FleetInfoView(context, FleetInfoViewModel.instance, vm)

    suspend fun draw(container: Container) {
        val tiledMap = resourcesVfs["world.tmx"].readTiledMap()
        val tileSize = tiledMap.tilewidth
        /*
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
        val gameMap = WorldMap(tiledMap.getMovableArea(), tiledMap.tileheight, portPositions)

        container.apply {
            // TODO change texture..
            val viewFleet = sprite(texture = resourcesVfs["S100.png"].readBitmap())
            val camera = camera {
                tiledMapView(tiledMap) {
                    addChild(viewFleet)
                }
            }
            camera.scale = viewScale

            // on update fleet position
            vm.fleet.observe {
                // move fleet view
                viewFleet.x = it.xy.x - viewFleet.width / 2
                viewFleet.y = it.xy.y - viewFleet.height / 2
                // centering camera
                camera.x = (camera.containerRoot.width / 2) - (it.xy.x * viewScale)
                camera.y = (camera.containerRoot.height / 2) - (it.xy.y * viewScale)
                // update context location
                context.location = it.xy
            }

            val background = SolidRect(width = mainWidth, height = mainHeight)

            // draw layer Fleet info
            val layerFleetInfo = fixedSizeContainer(windowWidth, windowHeight)
            fleetInfoView.draw(layerFleetInfo)
            vm.toggleFleetInfo.observe {
                layerFleetInfo.visible = it
            }

            tamraButton(
                    text = "항구 들어가기",
                    width = 140.0
            ).apply {
                alignX(background, 0.98, true)
                alignY(background, 0.99, true)
                onClick {
                    if (context.port != null) {
                        worldScene.sceneContainer.changeTo<PortScene>()
                    }
                }
                vm.toggleEnterPort.observe {
                    visible = it
                }
            }

            tamraButton(
                    width = 60.0, height = 40.0, textSize = 20.0,
                    text = "정보"
            ).apply {
                alignX(background, 0.02, true)
                alignY(background, 0.01, true)
                onClick {
                    vm.toggleFleetInfo(true)
                }
            }
        }

        // init vm fleet
        vm.fleet(PlayerFleet(xy = context.location, map = gameMap))
    }

}