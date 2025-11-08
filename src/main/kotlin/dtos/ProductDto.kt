package com.demo.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: String,
    val name: String,
    val basePrice: Double,
    val country: String,
    val discounts: Map<String, Double>,
    val finalPrice: Double
)