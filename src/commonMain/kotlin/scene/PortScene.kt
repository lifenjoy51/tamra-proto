package scene

import com.soywiz.klock.TimeSpan
import com.soywiz.korev.Key
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.TiledMap
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.Sprite
import com.soywiz.korge.view.addFixedUpdater
import com.soywiz.korge.view.solidRect
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import domain.GameContext
import domain.GameMap
import domain.XY
import domain.port.Player
import domain.port.PortTile
import util.SaveManager
import util.getMovableArea
import util.getObjectNames
import util.loadTiles

class PortScene(private val context: GameContext) : Scene() {

    private fun getTileTypes(tiledMap: TiledMap): Map<Int, PortTile> {
        val tileSet = tiledMap.tilesets.first().data    // 타일셋이 하나일 때.
        return tileSet.tiles.associate { (id, type) ->
            // 0은 없음을 나타냄. 그러므로 맵 데이터는 각 id+1.
            (id + 1) to PortTile.fromType(type)
        } + mapOf(0 to PortTile.EMPTY)
    }

    var currentBuilding = ""
    override suspend fun Container.sceneInit() {
        SaveManager.save(context).let {
            resourcesVfs["saved.json"].writeString(it)
        }


        val tiledMap = resourcesVfs["port.tmx"].readTiledMap()
        val tileSize = tiledMap.tilewidth

        val tileTypes = getTileTypes(tiledMap)
        val terrains = tiledMap.loadTiles("terrain", tileTypes)
        val sites = tiledMap.loadTiles("site", tileTypes)
        val movableArea = tiledMap.getMovableArea()
        val buildings = tiledMap.getObjectNames("buildings")

        val gameMap = GameMap(tiledMap.tileheight, movableArea)
        val player = Player(xy = XY(24.0, 24.0), map = gameMap)
        val viewPlayer = Sprite(resourcesVfs["player.png"].readBitmap()).apply {
            x = player.xy.x
            y = player.xy.y
        }

        val enterBuilding = solidRect(tileSize, tileSize / 2, Colors.DARKBLUE) {
            onClick {
                when (currentBuilding) {
                    "dock" -> sceneContainer.changeTo<WorldScene>()
                    else -> println(currentBuilding)
                }
            }
        }


        tiledMapView(tiledMap) {
            addChild(viewPlayer)
            addChild(enterBuilding)
        }.apply {
            scale = 6.0
        }

        println("port ${context.port}")

        var enterAvailable = false
        addFixedUpdater(TimeSpan(100.0)) {

            // 배 움직임
            player.apply {
                when {
                    views.input.keys[Key.RIGHT] -> moveRight()
                    views.input.keys[Key.LEFT] -> moveLeft()
                    views.input.keys[Key.UP] -> moveUp()
                    views.input.keys[Key.DOWN] -> moveDown()
                }
                viewPlayer.let {
                    // println("${it.x} ${xy.xd} ${it.y} ${xy.yd}")
                    if (it.x != xy.x || it.y != xy.y) {
                        it.x = xy.x
                        it.y = xy.y
                    }
                }

                enterAvailable = false
                val txy = xy.toTXY(tileSize)
                val buildingName = buildings[txy]
                if (buildingName != null) {
                    enterBuilding.x = txy.toXY(tileSize).x
                    enterBuilding.y = txy.toXY(tileSize).y
                    currentBuilding = buildingName
                    enterAvailable = true
                }

                enterBuilding.visible = enterAvailable
            }
        }
    }
}