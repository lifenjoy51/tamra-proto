import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.CellData
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.file.Paths

class Downloader {
    val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()
    private val applicationName = "Tamra Resource Downloader"
    private val storedTokenPath = "tokens"
    private val credentialsFilePath = "/credentials.json"
    private val spreadsheetId = "1PeVI4WIE_xAmGKzwqPcevQyvahIc3XMMrjws0A1vwNo"

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential {
        // Load client secrets.
        val inputStream: InputStream = Downloader::class.java.getResourceAsStream(credentialsFilePath)
        val clientSecrets: GoogleClientSecrets = GoogleClientSecrets.load(jsonFactory, InputStreamReader(inputStream))

        // Build flow and trigger user authorization request.
        val flow: GoogleAuthorizationCodeFlow = GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, jsonFactory, clientSecrets, listOf(SheetsScopes.SPREADSHEETS_READONLY))
            .setDataStoreFactory(FileDataStoreFactory(File(storedTokenPath)))
            .setAccessType("offline")
            .build()
        val receiver: LocalServerReceiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    fun down(): Map<String, MutableList<Map<String, Any>>> {
        // Build a new authorized API client service.
        val HTTP_TRANSPORT: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()

        val service: Sheets = Sheets.Builder(HTTP_TRANSPORT, jsonFactory, getCredentials(HTTP_TRANSPORT))
            .setApplicationName(applicationName)
            .build()
        val sheets = service.spreadsheets().get(spreadsheetId)
        sheets.includeGridData = true
        val response = sheets.execute()
        val data = mutableMapOf<String, MutableList<Map<String, Any>>>()
        response.sheets.forEach { sheet ->
            val title = sheet.properties["title"].toString()
            val sheetData: MutableList<Map<String, Any>> = mutableListOf()
            sheet.data.forEach { grid ->
                val header = grid.rowData[0].getValues().map { it.toHeader() }
                grid.rowData.forEachIndexed { i, rowData ->
                    if (i == 0) return@forEachIndexed
                    val row = mutableMapOf<String, Any>()
                    header.forEachIndexed { c, header ->
                        kotlin.runCatching {
                            row[header.name] = rowData.getValues()[c]?.formattedValue ?: ""
                        }.onFailure {
                            println("null ${header.name} $c $it")
                        }

                    }
                    sheetData.add(row)
                }
                data[title] = sheetData
            }
        }
        return data.toMap()
    }

    fun CellData.toHeader(): Header {
        val head = formattedValue.let { s -> s.substring(s.indexOf("#") + 1) }
        val hasLang = head.contains("@")
        val name = if (hasLang) {
            head.substring(0, head.indexOf("@"))
        } else {
            head
        }
        val lang = if (hasLang) {
            head.substring(head.indexOf("@") + 1)
        } else {
            null
        }
        return Header(
            name, lang
        )
    }

}

data class Header(
    val name: String,
    val lang: String?
)

fun main() {
    val downloader = Downloader()
    val data = downloader.down()
    val jsonString = downloader.jsonFactory.toString(data)
    val jsonPath = Paths.get("src", "commonMain", "resources", "data.json")
    val jsonFile = jsonPath.toFile()
    jsonFile.writeText(jsonString)
}