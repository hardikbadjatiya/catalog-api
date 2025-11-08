package com.demo.entity

import java.util.concurrent.ConcurrentHashMap
import kotlin.math.round

data class Product(
    val id: String,
    val name: String,
    val basePrice: Double,
    val country: String,
) {
    private val discountMap = ConcurrentHashMap<String, Double>()

    val discounts: Map<String, Double>
        get() = discountMap.toMap()

    fun applyDiscount(discountId: String, percent: Double): Boolean {
        require(percent > 0.0 && percent <= 100.0) { "percent must be >0 and <100" }
        return discountMap.putIfAbsent(discountId, percent) == null
    }

    fun finalPrice(vatPercent: Double): Double {
        val discountFactor = if (discountMap.isEmpty()) 1.0
        else discountMap.values.fold(1.0) { acc, p -> acc * (1.0 - p / 100.0) }

        val afterDiscount = basePrice * discountFactor
        val afterVat = afterDiscount * (1.0 + vatPercent / 100.0)
        return round(afterVat * 100.0) / 100.0
    }
}