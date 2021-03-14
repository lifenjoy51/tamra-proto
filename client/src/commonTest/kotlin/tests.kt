import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tamra.common.Site
import tamra.common.SiteId
import kotlin.test.Test

class JsonTest {
    @Test
    fun testJson() {
        // Serializing objects
        val data = Site(SiteId.EXIT, "name", "sub")
        val string = Json.encodeToString(data)
        println(string) // {"name":"kotlinx.serialization","language":"Kotlin"}
        // Deserializing back into objects
        val obj = Json.decodeFromString<Site>(string)
        println(obj) // Project(name=kotlinx.serialization, language=Kotlin)
    }
}