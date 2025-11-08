package com.demo.routes

import com.demo.dtos.DiscountDto
import com.demo.service.CatalogService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.catalogRoutes() {

    val catalogService = CatalogService()

    route("/products") {

        get {
            val country = call.request.queryParameters["country"]
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "country query parameter is required")
                )

            try {
                val products = catalogService.getProductsByCountry(country)
                call.respond(HttpStatusCode.OK, products)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        put("/{id}/discount") {
            val productId = call.parameters["id"]
                ?: return@put call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Missing product ID in path")
                )

            val request = try {
                call.receive<DiscountDto>()
            } catch (e: Exception) {
                return@put call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Invalid request body")
                )
            }

            try {
                val response = catalogService.applyDiscount(productId, request)
                val status = if (response["status"] == "discount_applied") HttpStatusCode.Created else HttpStatusCode.OK
                call.respond(status, response)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: NoSuchElementException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
            }
        }
    }
}