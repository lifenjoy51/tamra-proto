package scene.world

import ViewModelProvider
import com.soywiz.korge.input.onClick
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.Matrix
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.unaryMinus
import domain.GameData
import domain.LandingId
import domain.PortId
import domain.world.WorldMap
import mainHeight
import mainWidth
import scene.common.HeaderView
import ui.tamraButton
import ui.tamraRect
import util.getMovableArea
import util.getObjectNames

const val viewScale = 3.0

class WorldView(
    viewModelProvider: ViewModelProvider,
    private val changePortScene: suspend () -> Unit,
    private val changeLandingScene: suspend () -> Unit
) {
    private val vm: WorldViewModel = viewModelProvider.worldViewModel
    private val headerView = HeaderView(viewModelProvider)

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
        val overlays = tiledMap.loadTiles("overlay", tileTypeMap)
        */
        val portPositions = tiledMap.getObjectNames("ports").mapValues { (k, v) ->
            GameData.ports[PortId.valueOf(v)]
        }
        val landingPositions = tiledMap.getObjectNames("landings").mapValues { (k, v) ->
            LandingId.valueOf(v)
        }
        val worldMap = WorldMap(tiledMap.getMovableArea(), tiledMap.tileheight, portPositions, landingPositions)

        container.fixedSizeContainer(mainWidth, mainWidth, clip = true) {
            positionY(100)
            tamraRect(width = width, height = height, color = Colors.SKYBLUE)

            // TODO change texture..
            val fleetView = sprite(texture = resourcesVfs["S100.png"].readBitmap())
            val tileMapView = tiledMapView(tiledMap) {
                addChild(fleetView)
                scale = viewScale
            }
            //addChild(tileMapView)

            // on update fleet position
            vm.playerFleet.observe { fleet ->

                // move fleet view
                with(fleetView) {
                    rotation(fleet.angle.unaryMinus())
                    Point(width, height)
                        .rotate(fleet.angle.unaryMinus())
                        .let { p ->
                            x = fleet.point.x - p.x / 2
                            y = fleet.point.y - p.y / 2
                        }
                }

                // centering camera
                with(tileMapView) {
                    val rotatedPoint = fleet.point
                        .rotate(fleet.angle)
                        .mul(viewScale)
                    setTransform(Matrix.Transform(
                        x = width / 2 - rotatedPoint.x,
                        y = height / 2 - rotatedPoint.y,
                        scaleX = viewScale,
                        scaleY = viewScale,
                        rotation = fleet.angle
                    ))
                }

            }
        }

        // ui
        container.apply {

            val background = SolidRect(width = mainWidth, height = mainHeight)

            // draw header
            headerView.draw(container)

            tamraButton(text = "항구 들어가기", width = 120.0, px = mainWidth - 130, py = mainHeight - 40) {
                onClick {
                    if (!vm.nearPort.value.isNullOrEmpty()) {
                        vm.enterPort()
                        changePortScene.invoke()
                    }
                }
                vm.nearPort.observe { visible = it.isNotEmpty() }
            }

            tamraButton(text = "상륙하기", width = 120.0, px = mainWidth - 130, py = mainHeight - 40) {
                onClick {
                    if (!vm.nearLanding.value.isNullOrEmpty()) {
                        vm.enterLanding()
                        changeLandingScene.invoke()
                    }
                }
                vm.nearLanding.observe { visible = it.isNotEmpty() }
            }
        }

        // init vm fleet
        vm.initPlayerFleet(worldMap)
    }

}