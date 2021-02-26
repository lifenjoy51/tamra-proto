package scene.world

import ViewModelProvider
import com.soywiz.korge.component.onStageResized
import com.soywiz.korge.input.onClick
import com.soywiz.korge.tiled.TiledMap
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Matrix
import com.soywiz.korma.geom.plus
import com.soywiz.korma.math.roundDecimalPlaces
import domain.*
import domain.world.PlayerFleet
import domain.world.WorldMap
import mainHeight
import mainWidth
import scene.common.HeaderView
import ui.Pseudo3DFilter
import ui.tamraButton
import ui.tamraRect
import ui.tamraText
import util.getObjectNames
import util.toPoint

class WorldView(
    viewModelProvider: ViewModelProvider,
    private val changePortScene: suspend () -> Unit,
    private val changeLandingScene: suspend () -> Unit
) {
    private val vm: WorldViewModel = viewModelProvider.worldViewModel
    private val headerView = HeaderView(viewModelProvider)

    suspend fun draw(container: Container) {
        val tiledMap = resourcesVfs["world.tmx"].readTiledMap()
        val tileSet = tiledMap.tilesets.first().data    // 타일셋이 하나일 때.
        // tile마다 컬리전 정보를 갖고있다.
        //val tileCollision = tileSet.tiles.associate { it.id + 1 to it.objectGroup }
        /*
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
        val tiles: Map<TileXY, Int> = tiledMap.data.tileLayers.find { it.name == "terrain" }!!.let {
            val m: MutableMap<TileXY, Int> = mutableMapOf()
            for (x in 0 until it.width) {
                for (y in 0 until it.height) {
                    m[TileXY(x, y)] = it.get(x, y)
                }
            }
            m
        }
        val tileCollisions = tileSet.tiles.associate { tileData ->
            val list = tileData.objectGroup?.objects?.map {
                (it.objectType as TiledMap.Object.Type.Polygon).points.map { p ->
                    LocationXY(p.x + it.bounds.left, p.y + it.bounds.top)
                }
            }?.map { list ->
                list.mapIndexed { i: Int, point: LocationXY ->
                    var t = if (i == 0) list.size - 1 else i - 1
                    point to list[t]
                }
            } ?: emptyList()
            tileData.id + 1 to list
        }

        val worldMap = WorldMap(portPositions, landingPositions, tiles, tileCollisions)

        // background
        container.tamraRect(width = mainWidth.toDouble(), height = mainHeight.toDouble(), color = Colors.DIMGREY)

        val mainSize = mainWidth.toDouble()
        container.fixedSizeContainer(mainWidth, mainWidth, clip = true) {
            positionY(32)
            // 배경
            tamraRect(width = mainSize, height = mainSize, color = Colors["#e8f1f4"])
            // pos = tile * size.
            val tileMapView = tiledMapView(tiledMap) {
                onStageResized { width, height ->
                    println("$width $height")
                    filter = Pseudo3DFilter(width.toDouble(), height.toDouble())
                }
            }

            // 하늘
            sprite(texture = resourcesVfs["sky.png"].readBitmap()) {
                scaledWidth = mainSize
                scaledHeight = mainSize / 3
            }

            // TODO change texture..
            sprite(texture = resourcesVfs["S200.png"].readBitmap()) {
                scale = 4.0 / vm.viewScale.get()
                center()
                position(mainWidth / 2, mainWidth - 40)
            }

            // on update fleet position
            vm.playerFleet.observe { fleet ->
                //println(fleet.location)
                // 하단 중앙을 기점으로 한다.
                with(tileMapView) {
                    val rotatedPoint = (fleet.location.toPoint() - baseCoord.point.toPoint())
                        .rotate(fleet.angle)
                        .mul(vm.viewScale.get())
                    val t = Matrix.Transform(
                        x = -rotatedPoint.x + mainWidth / 2,
                        y = -rotatedPoint.y + mainWidth * 8 / 10,
                        scaleX = vm.viewScale.get(),
                        scaleY = vm.viewScale.get(),
                        rotation = fleet.angle
                    )
                    setTransform(t)
                }

            }
        }

        // ui
        container.apply {

            // draw header
            val header = headerView.draw(container)

            // controls
            tamraText(text = "시속", px = 10, py = mainHeight - 170)
            tamraText(text = "", px = 50, py = mainHeight - 170) {
                vm.playerFleet.observe {
                    setText("${it.velocity.roundDecimalPlaces(2)}")
                }
            }
            tamraText(text = "", px = 100, py = mainHeight - 170) {
                vm.playerFleet.observe {
                    val state = when (it.sailState) {
                        PlayerFleet.SaleState.FULL_SALE -> "펼침"
                        PlayerFleet.SaleState.CLOSE_SALE -> "접힘"
                        PlayerFleet.SaleState.STOP -> "정박"
                    }
                    setText("$state")
                }
            }

            // 풍향을 어떻게 표시할까?
            fixedSizeContainer(width = 90, height = 90) {
                positionX(10)
                positionY(mainHeight - 140)
                tamraRect(width = width, height = height, color = Colors.SKYBLUE)
                sprite(resourcesVfs["arrow.png"].readBitmap(), anchorX = 0.5, anchorY = 0.5) {
                    alpha(0.3)
                    positionX(45)
                    positionY(45)
                    // 풍향이 바뀌거나... or 배의 방향이 바뀌면 달라져야하는데 -_ -
                    vm.playerFleet.observe {
                        rotation(it.angle.plus(vm.windDirection.value ?: Angle.ZERO))
                    }
                    vm.windDirection.observe {
                        rotation(it.plus(vm.playerFleet.value?.angle ?: Angle.ZERO))
                    }

                }
            }


            tamraButton(text = "<<", width = 40.0, px = 10, py = mainHeight - 40) {
                onClick {
                    vm.turnLeft()
                }
            }
            tamraButton(text = ">>", width = 40.0, px = 60, py = mainHeight - 40) {
                onClick {
                    vm.turnRight()
                }
            }

            tamraButton(text = "정박", width = 50.0, px = mainWidth - 120, py = mainHeight - 40) {
                onClick {
                    vm.stop()
                }
            }
            tamraButton(text = "펼침", width = 50.0, px = mainWidth - 60, py = mainHeight - 40) {
                onClick {
                    vm.controlSail()
                }
                vm.playerFleet.observe {
                    val state = when (it.sailState) {
                        PlayerFleet.SaleState.FULL_SALE -> "접기"
                        PlayerFleet.SaleState.CLOSE_SALE -> "펴기"
                        PlayerFleet.SaleState.STOP -> "이동"
                    }
                    setText("$state")
                }
            }

            tamraButton(text = "항구 들어가기", width = 120.0, px = mainWidth - 130, py = mainHeight - 170) {
                onClick {
                    if (!vm.nearPort.value.isNullOrEmpty()) {
                        vm.enterPort()
                        changePortScene.invoke()
                    }
                }
                vm.nearPort.observe { visible = it.isNotEmpty() }
            }

            tamraButton(text = "상륙하기", width = 120.0, px = mainWidth - 130, py = mainHeight - 170) {
                onClick {
                    if (!vm.nearLanding.value.isNullOrEmpty()) {
                        vm.enterLanding()
                        changeLandingScene.invoke()
                    }
                }
                vm.nearLanding.observe { visible = it.isNotEmpty() }
            }

            sendChildToFront(header)
        }

        // init vm fleet
        vm.initPlayerFleet(worldMap)

        // 리사이즈를 해줘야 맵이 정상적으로 보임... 원인불명.
        views().resized()
    }

}