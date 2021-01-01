import com.soywiz.korge.Korge
import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korim.font.readTtfFont
import com.soywiz.korim.format.readBitmap
import com.soywiz.korinject.AsyncInjector
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.serialization.json.Json
import com.soywiz.korma.geom.SizeInt
import domain.*
import scene.MainScene
import scene.port.PortScene
import scene.port.PortViewModel
import scene.port.market.MarketScene
import scene.port.market.MarketViewModel
import scene.world.FleetInfoViewModel
import scene.world.WorldScene
import scene.world.WorldViewModel
import ui.TamraFont
import util.SaveManager
import kotlin.reflect.KClass

suspend fun main() = Korge(Korge.Config(module = TamraModule))

const val mainWidth = 288
const val mainHeight = 512
const val windowWidth = mainWidth * 4 / 5
const val windowHeight = mainHeight * 4 / 5

object TamraModule : Module() {
    // override val mainScene: KClass<out Scene> = MainScene::class
    // override val mainScene: KClass<out Scene> = WorldScene::class
    override val mainScene: KClass<out Scene> = PortScene::class
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
        mapPrototype { MainScene(get()) }
        mapPrototype { WorldScene(get(), get()) }
        mapPrototype { PortScene(get(), get()) }
        mapPrototype { MarketScene(get(), get()) }
    }

    private fun initViewModels(store: GameStore): ViewModelProvider {
        return ViewModelProvider(
            WorldViewModel(store),
            FleetInfoViewModel(store),
            PortViewModel(store),
            MarketViewModel(store)
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
                ships = mutableListOf(
                    GameData.blueprints.getValue(ShipType.CHOMASUN).makeShip("첫배"),
                    GameData.blueprints.getValue(ShipType.DOTBAE).makeShip("짐배")
                ),
                money = 1000,
                port = PortId.JEJU,
                location = XY(100.0, 70.0),
                marketStates = GameData.ports.flatMap { (k, v) ->
                    v.market.marketProducts.map { (id, mp) -> (k to id) to mp.marketState }
                }.associate { it.first to it.second },
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
        GameData.init(
            products = products,
            ports = ports,
            shipBlueprints = shipBlueprints
        )
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
                        product = allProducts.getValue(productId),
                        marketState = MarketState(
                            marketSize = marketSize,
                            marketStock = marketSize,
                            supplyAndDemand = supplyAndDemand
                        )
                    )
                }
            val portMarket = Market(
                products
            )

            val shipsOnSale = data.getValue("ports_ships")
                .filter { it["portId"] == portId }.map {
                    val shipType = ShipType.valueOf(it.getValue("shipType"))
                    allShips.getValue(shipType)
                }
            val portShipYard = ShipYard(
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