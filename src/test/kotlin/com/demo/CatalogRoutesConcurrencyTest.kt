package com.demo

import com.demo.dtos.DiscountDto
import com.demo.repo.ProductRepo
import com.demo.routes.catalogRoutes
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CatalogRoutesConcurrencyTest {

    @BeforeEach
    fun setup() {
        ProductRepo.initSampleData()
    }

    @Test
    fun `should apply discount only once even under concurrent requests`() = testApplication {
        application {
            install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
            routing { catalogRoutes() }
        }

        val client = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
            expectSuccess = false
            defaultRequest {
                header(HttpHeaders.Accept, ContentType.Application.Json)
                contentType(ContentType.Application.Json)
            }
        }

        val productId = "p-swe-01"
        val discountRequest = DiscountDto("DISC10", 10.0)
        val concurrencyLevel = 30

        val responses = coroutineScope {
            (1..concurrencyLevel).map {
                async {
                    client.put("/products/$productId/discount") {
                        setBody(discountRequest)
                    }
                }
            }.awaitAll()
        }

        val bodies = responses.map { it.bodyAsText() }
        val successCount = bodies.count { it.contains("discount_applied") }
        val alreadyCount = bodies.count { it.contains("already_applied") }

        println("Applied $successCount, already applied: $alreadyCount")
        assertEquals(1, successCount)
        assertTrue(alreadyCount >= concurrencyLevel - 1)
    }
}
