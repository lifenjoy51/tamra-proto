import com.soywiz.korge.Korge
import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korim.font.readTtfFont
import com.soywiz.korim.format.readBitmap
import com.soywiz.korinject.AsyncInjector
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.serialization.json.Json
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.SizeInt
import domain.*
import domain.event.*
import domain.port.market.Market
import domain.port.market.MarketProduct
import domain.port.market.MarketProductState
import domain.port.shipyard.ShipBlueprint
import domain.port.shipyard.Shipyard
import scene.MainScene
import scene.common.FleetInfoViewModel
import scene.common.HeaderViewModel
import scene.event.EventViewModel
import scene.landing.LandingScene
import scene.landing.LandingViewModel
import scene.port.PortScene
import scene.port.PortViewModel
import scene.port.market.MarketBuyViewModel
import scene.port.market.MarketScene
import scene.port.market.MarketSellViewModel
import scene.port.shipyard.ShipyardBuyViewModel
import scene.port.shipyard.ShipyardScene
import scene.port.shipyard.ShipyardSellViewModel
import scene.world.WorldScene
import scene.world.WorldViewModel
import ui.TamraFont
import util.SaveManager
import kotlin.reflect.KClass

suspend fun main() = Korge(Korge.Config(module = TamraModule))

const val defaultMargin = 8
const val infoAreaHeight = 30
const val itemAreaHeight = 60
const val textTabSpace = 60

const val mainWidth = 288
const val mainHeight = 512
const val windowWidth = mainWidth * 4 / 5
const val windowHeight = mainHeight * 4 / 5 - defaultMargin * 2

object TamraModule : Module() {
    // override val mainScene: KClass<out Scene> = MainScene::class
    override val mainScene: KClass<out Scene> = WorldScene::class

    // override val mainScene: KClass<out Scene> = PortScene::class
    override val size: SizeInt = SizeInt(mainWidth, mainHeight)

    override suspend fun AsyncInjector.configure() {
        // loading..
        loadFont()
        loadGameData()

        //
        val store = loadStore()
        mapInstance(store)

        val viewModelProvider = initViewModels(store)
        mapInstance(viewModelProvider)

        // map scenes
        mapPrototype { MainScene() }
        mapPrototype { WorldScene(get()) }
        mapPrototype { PortScene(get()) }
        mapPrototype { MarketScene(get()) }
        mapPrototype { ShipyardScene(get()) }
        mapPrototype { LandingScene(get()) }
    }

    private fun initViewModels(store: GameStore): ViewModelProvider {
        return ViewModelProvider(
            HeaderViewModel(store),
            WorldViewModel(store),
            FleetInfoViewModel(store),
            PortViewModel(store),
            MarketBuyViewModel(store),
            MarketSellViewModel(store),
            ShipyardBuyViewModel(store),
            ShipyardSellViewModel(store),
            EventViewModel(store),
            LandingViewModel(store)
        )
    }

    private suspend fun loadFont() {
        // https://software.naver.com/software/summary.nhn?softwareId=GWS_003430&categoryId=I0100000#
        val ttf = resourcesVfs["today.ttf"].readTtfFont()
        TamraFont.init(ttf)
    }

    private suspend fun loadStore(): GameStore {
        val savedGameJsonString = resourcesVfs["saved.json"].readString()
        return if (savedGameJsonString.isEmpty()) {
            GameStore(
                fleet = Fleet(
                    ships = mutableListOf(
                        GameData.blueprints.getValue(ShipType.CHOMASUN).build("첫배"),
                        GameData.blueprints.getValue(ShipType.DOTBAE).build("짐배")
                    ),
                    balance = 1000,
                    port = PortId.JEJU,
                    location = Point(100.0, 70.0),
                    cargoItems = mutableListOf()
                )
            )
        } else {
            SaveManager.load(savedGameJsonString)
        }
    }

    private suspend fun loadGameData() {
        val dataJsonString = resourcesVfs["data.json"].readString()
        val data: Map<String, MutableList<Map<String, String>>> =
            Json.parse(dataJsonString) as Map<String, MutableList<Map<String, String>>>
        val products = loadProducts(data)
        val shipBlueprints = loadShipBlueprints(data)
        val ports = loadPorts(data, products, shipBlueprints)
        val conditions = loadConditions(data)
        val contents = loadContents(data)
        val events = loadEvents(data, conditions, contents)
        GameData.init(
            products = products,
            ports = ports,
            shipBlueprints = shipBlueprints,
            conditions = conditions,
            events = events
        )
    }

    private fun loadConditions(data: Map<String, MutableList<Map<String, String>>>): Map<String, EventCondition> {
        return data.getValue("eventConditions").associate { conditionData ->
            val id = conditionData.getValue("id")
            val x = conditionData.getValue("x").let(Subject.Companion::parse)
            val op = Op.valueOf(conditionData.getValue("op"))
            val y = conditionData.getValue("y").let(Subject.Companion::parse)
            id to EventCondition(
                id = id,
                x = x,
                op = op,
                y = y
            )
        }
    }

    private fun loadContents(data: Map<String, MutableList<Map<String, String>>>): Map<String, List<EventContent>> {
        return data.getValue("eventContents").map { contentData ->
            val type = ContentType.valueOf(contentData.getValue("type"))
            val eventId = contentData.getValue("eventId")
            val position = contentData["position"]?.let { ContentPosition.valueOf(it) } ?: ContentPosition.C
            val speaker = contentData["speaker"] ?: ""
            val lines = contentData.getValue("lines")
            when (type) {
                ContentType.N -> Narration(
                    eventId = eventId,
                    position = position,
                    lines = lines
                )
                ContentType.C -> Conversation(
                    eventId = eventId,
                    position = position,
                    speaker = speaker,
                    lines = lines
                )
            }.apply { Unit }
        }.groupBy { it.eventId }
    }

    private fun loadEvents(data: Map<String, MutableList<Map<String, String>>>, conditions: Map<String, EventCondition>, contents: Map<String, List<EventContent>>): Map<String, GameEvent> {
        return data.getValue("events").associate { eventData ->
            val eventId = eventData.getValue("id")
            val location = EventLocation.valueOf(eventData.getValue("location"))
            val conditionId = eventData.getValue("conditionId")

            eventId to GameEvent(
                id = eventId,
                location = location,
                condition = conditions.getValue(conditionId),
                contents = contents.getValue(eventId)
            )
        }
    }

    private fun loadProducts(data: Map<String, MutableList<Map<String, String>>>): Map<ProductId, Product> {
        return data.getValue("products").associate { productData ->
            val productId = ProductId.valueOf(productData.getValue("productId"))
            val productType = ProductType.valueOf(productData.getValue("productType"))
            val productName = productData.getValue("name")
            val price = productData.getValue("price").toInt()

            productId to Product(
                id = productId,
                type = productType,
                name = productName,
                price = price
            )
        }
    }

    private suspend fun loadShipBlueprints(data: Map<String, MutableList<Map<String, String>>>): Map<ShipType, ShipBlueprint> {
        return data.getValue("ships").associate { shipData ->
            val shipType = ShipType.valueOf(shipData.getValue("shipType"))
            val imgName = shipData.getValue("imgName")
            val imgSprite = resourcesVfs[imgName].readBitmap()
            val shipTypeName = shipData.getValue("typeName")
            val cargoSize = shipData.getValue("cargoSize").toInt()
            val speed = shipData.getValue("speed").toInt()
            val price = shipData.getValue("price").toInt()

            shipType to ShipBlueprint(
                shipType, shipTypeName, imgSprite, cargoSize, speed, price
            )
        }
    }

    private fun loadPorts(
        data: Map<String, MutableList<Map<String, String>>>,
        allProducts: Map<ProductId, Product>,
        allShips: Map<ShipType, ShipBlueprint>
    ): Map<PortId, Port> {
        return data.getValue("ports").associate { portData ->
            val portId = portData.getValue("id")
            val portName = portData.getValue("name")

            val products = data.getValue("ports_products")
                .filter { it["portId"] == portId }.associate {
                    val productId = ProductId.valueOf(it.getValue("productId"))
                    val marketSize = it.getValue("marketSize").toString().toInt()
                    val supplyAndDemand = it.getValue("supplyAndDemand").toString().toInt()

                    productId to MarketProduct(
                        id = productId,
                        state = MarketProductState(
                            marketSize = marketSize,
                            marketStock = marketSize,
                            supplyAndDemand = supplyAndDemand
                        )
                    )
                }
            val portMarket = Market(
                PortId.valueOf(portId),
                products
            )

            val shipsOnSale = data.getValue("ports_ships")
                .filter { it["portId"] == portId }.map {
                    val shipType = ShipType.valueOf(it.getValue("shipType"))
                    allShips.getValue(shipType)
                }
            val portShipYard = Shipyard(
                PortId.valueOf(portId),
                shipsOnSale
            )

            PortId.valueOf(portId) to Port(
                PortId.valueOf(portId),
                portName,
                portMarket,
                portShipYard
            )
        }
    }
}