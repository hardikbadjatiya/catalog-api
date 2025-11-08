package com.demo

import com.demo.plugins.configureHTTP
import com.demo.plugins.configureRouting
import com.demo.plugins.configureSerialization
import com.demo.plugins.configureSwagger
import com.demo.repo.ProductRepo
import com.demo.routes.catalogRoutes
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing

fun main() {
    ProductRepo.initSampleData()
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(ContentNegotiation) { json() }
        routing { catalogRoutes() }
    }.start(wait = true)
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureSwagger()
    configureRouting()
}
