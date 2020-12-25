package scene

import com.soywiz.klock.TimeSpan
import com.soywiz.korev.Key
import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onOut
import com.soywiz.korge.input.onOver
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.Colors.BLACK
import com.soywiz.korim.color.Colors.BLANCHEDALMOND
import com.soywiz.korim.color.Colors.RED
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import domain.*
import domain.world.PlayerFleet
import domain.world.WorldTile
import mainHeight
import mainWidth
import ui.TamraButton
import util.getMovableArea
import util.getObjectNames
import util.loadTiles
import windowHeight
import windowWidth

const val viewScale = 4.0

class WorldScene(private val context: GameContext) : Scene() {

    lateinit var btnEnterPort: TamraButton
    lateinit var btnViewFleetInfo: TamraButton
    lateinit var viewFleet: Sprite
    lateinit var layerFleetInfo: Container


    override suspend fun Container.sceneInit() {
        val scene = this
        val tiledMap = resourcesVfs["world.tmx"].readTiledMap()
        val tileSize = tiledMap.tilewidth
        val tileSet = tiledMap.tilesets.first().data    // 타일셋이 하나일 때.
        val tileTypeMap = tileSet.tiles.associate { (id, type) ->
            // 0은 없음을 나타냄. 그러므로 맵 데이터는 각 id+1.
            (id + 1) to WorldTile.fromType(type)
        } + mapOf(0 to WorldTile.EMPTY)

        val terrains = tiledMap.loadTiles("terrain", tileTypeMap)
        val sites = tiledMap.loadTiles("site", tileTypeMap)
        val movableArea = tiledMap.getMovableArea()
        val portPositions = tiledMap.getObjectNames("ports").mapValues { (k, v) ->
            GameData.ports[PortId.valueOf(v)]
        }

        val gameMap = GameMap(tiledMap.tileheight, movableArea)
        val fleet = PlayerFleet(xy = context.location, map = gameMap)
        viewFleet = Sprite(resourcesVfs["S100.png"].readBitmap()).apply {
            x = fleet.xy.x - width / 2
            y = fleet.xy.y - height / 2
        }

        val camera = camera {
            tiledMapView(tiledMap) {
                addChild(viewFleet)
            }
        }
        camera.scale = viewScale


        // ui..
        val background = SolidRect(width = mainWidth, height = mainHeight)

        btnEnterPort = TamraButton(
            text = "항구 들어가기",
            width = 140.0
        ).apply {
            alignX(background, 0.98, true)
            alignY(background, 0.99, true)
            onClick {
                if (context.port != null) {
                    sceneContainer.changeTo<PortScene>()
                }
            }
        }

        btnViewFleetInfo = TamraButton(
            width = 60.0, height = 40.0, textSize = 20.0,
            text = "정보"
        ).apply {
            alignX(background, 0.02, true)
            alignY(background, 0.01, true)
            onClick {
                layerFleetInfo.visible(true)
            }
        }

        layerFleetInfo = container {
            visible = false
            val back = solidRect(windowWidth, windowHeight, color = RGBA(0,187, 255, 240)) {

                centerOnStage()
            }

            text("X", color = BLACK, textSize = 24.0) {
                alignX(back, 0.98, true)
                alignY(back, 0.02, true)
                alpha = 0.7
                onOut { this.alpha = 0.7 }
                onOver { this.alpha = 1.0 }
                onClick {
                    layerFleetInfo.visible(false)
                }
            }
        }

        addChild(btnEnterPort)
        addChild(btnViewFleetInfo)
        addChild(layerFleetInfo)


        //
        addFixedUpdater(TimeSpan(100.0)) {
            // 배 움직임
            moveFleet(fleet)

            // 주변항구 검색.
            scanNearPort(fleet.xy.toTXY(tileSize), portPositions)

            // 카메라를 항상 중심으로.
            centeringCamera(camera)
        }
    }

    private fun moveFleet(fleet: PlayerFleet) {
        fleet.apply {
            when {
                views.input.keys[Key.RIGHT] -> moveRight()
                views.input.keys[Key.LEFT] -> moveLeft()
                views.input.keys[Key.UP] -> moveUp()
                views.input.keys[Key.DOWN] -> moveDown()
            }
            viewFleet.let {
                if (it.x != xy.x || it.y != xy.y) {
                    it.x = xy.x - it.width / 2
                    it.y = xy.y - it.height / 2
                }
            }

            context.location = xy
        }
    }

    private fun scanNearPort(txy: TXY, portPositions: Map<TXY, Port?>) {
        // 항구가 있으면 입항 표시.
        val portIds = txy.crossXY.mapNotNull { txy -> portPositions[txy]?.id }
        context.port = portIds.firstOrNull()
        btnEnterPort.visible = portIds.isNotEmpty()
    }

    private fun centeringCamera(camera: Camera) {
        camera.x = (camera.containerRoot.width / 2) - (context.location.x * viewScale)
        camera.y = (camera.containerRoot.height / 2) - (context.location.y * viewScale)

    }
}