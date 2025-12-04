package web

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.client.RestTemplate
import web.client.ApiClient
import web.client.api.DefaultApi

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("OpenAPI Generated Client Tests")
class OpenApiGeneratedClientTest {

    @LocalServerPort
    private var port: Int = 0

    private lateinit var webApi: DefaultApi
    private lateinit var apiClient: ApiClient

    @BeforeEach
    fun setup() {
        apiClient = ApiClient(RestTemplate())
        apiClient.setBasePath("http://localhost:$port")
        webApi = DefaultApi(apiClient)
    }

    @Test
    @DisplayName("Should retrieve account by number using generated client")
    fun testFindAccountByNumber() {
        // Given: Un número de cuenta válido
        val accountNumber = "123456789"

        // When: Llamamos al endpoint usando el cliente generado
        val response = webApi.byNumber(accountNumber)

        // Then: Verificamos que la respuesta es exitosa
        assertNotNull(response, "Response should not be null")
        assertTrue(response.isNotEmpty(), "Response should contain content")
    }

    @Test
    @DisplayName("Should search accounts by owner name using generated client")
    fun testSearchAccountsByOwner() {
        // Given: Un nombre parcial de propietario
        val ownerName = "Keri"

        // When: Buscamos cuentas por propietario
        val response = webApi.ownerSearch(ownerName)

        // Then: Verificamos que se encontraron cuentas
        assertNotNull(response, "Response should not be null")
        assertTrue(response.isNotEmpty(), "Response should contain content")
    }

    @Test
    @DisplayName("Should handle non-existent account number gracefully")
    fun testNonExistentAccount() {
        // Given: Un número de cuenta que no existe
        val invalidAccountNumber = "999999999"

        // When: Llamamos al endpoint con cuenta inválida
        val response = webApi.byNumber(invalidAccountNumber)

        // Then: Verificamos que devuelve respuesta (puede ser un fallback)
        assertNotNull(response, "Response should not be null even for non-existent account")
    }

    @Test
    @DisplayName("Should display search form")
    fun testSearchForm() {
        // When: Accedemos al formulario de búsqueda
        val response = webApi.searchForm()

        // Then: Verificamos que devuelve la vista del formulario
        assertNotNull(response, "Response should not be null")
        assertTrue(response.isNotEmpty(), "Response should contain search form")
    }

    @Test
    @DisplayName("Should handle account home page")
    fun testGoHome() {
        // When: Accedemos a la página principal
        val response = webApi.goHome()

        // Then: Verificamos que devuelve la página de inicio
        assertNotNull(response, "Response should not be null")
        assertTrue(response.isNotEmpty(), "Response should contain home page")
    }

    @Test
    @DisplayName("Should execute search with account number")
    fun testDoSearchByAccountNumber() {
        // Given: Un número de cuenta
        val accountNumber = "123456789"

        // When: Ejecutamos la búsqueda
        val response = webApi.doSearch(accountNumber, null)

        // Then: Verificamos el resultado
        assertNotNull(response, "Response should not be null")
        assertTrue(response.isNotEmpty(), "Response should contain search results")
    }

    @Test
    @DisplayName("Should execute search with owner name")
    fun testDoSearchByOwnerName() {
        // Given: Un nombre de propietario
        val searchText = "Keri"

        // When: Ejecutamos la búsqueda
        val response = webApi.doSearch(null, searchText)

        // Then: Verificamos el resultado
        assertNotNull(response, "Response should not be null")
        assertTrue(response.isNotEmpty(), "Response should contain search results")
    }

    @Test
    @DisplayName("Should verify API base path configuration")
    fun testApiClientConfiguration() {
        // Given/When: El cliente está configurado
        val basePath = apiClient.basePath

        // Then: Verificamos la configuración
        assertEquals(
            "http://localhost:$port",
            basePath,
            "Base path should be correctly configured"
        )
    }
}
