package com.demo.repo

import com.demo.entity.Product
import java.util.concurrent.ConcurrentHashMap


object ProductRepo {
    private val products = ConcurrentHashMap<String, Product>()


    val vatByCountry = mapOf(
        "Sweden" to 25.0,
        "Germany" to 19.0,
        "France" to 20.0
    )


    fun initSampleData() {
        products.clear()
        listOf(
        Product("p-swe-01", "Swedish Chair", 100.0, "Sweden"),
        Product("p-swe-02", "Swedish Sofa", 450.0, "Sweden"),
        Product("p-swe-03", "Swedish Dining Table", 800.0, "Sweden"),
        Product("p-swe-04", "Swedish Bookshelf", 250.0, "Sweden"),

        Product("p-ger-01", "German Table", 200.0, "Germany"),
        Product("p-ger-02", "German Recliner", 500.0, "Germany"),
        Product("p-ger-03", "German Wardrobe", 850.0, "Germany"),
        Product("p-ger-04", "German Office Desk", 320.0, "Germany"),

        Product("p-fra-01", "French Lamp", 50.0, "France"),
        Product("p-fra-02", "French Sofa", 600.0, "France"),
        Product("p-fra-03", "French Bed Frame", 700.0, "France"),
        Product("p-fra-04", "French Coffee Table", 180.0, "France"),
        ).forEach { products[it.id] = it }
    }


    fun getProductsByCountry(country: String): List<Product> = products.values.filter { it.country == country }


    fun findProduct(id: String): Product? = products[id]
}