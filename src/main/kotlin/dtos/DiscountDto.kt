package com.demo.dtos

import kotlinx.serialization.Serializable

@Serializable
data class DiscountDto(
    val discountId: String,
    val percent: Double
)
