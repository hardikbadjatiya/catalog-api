package com.demo.service

import com.demo.dtos.DiscountDto
import com.demo.dtos.ProductDto
import com.demo.repo.ProductRepo

class CatalogService {

    fun getProductsByCountry(country: String): List<ProductDto> {
        val vat = ProductRepo.vatByCountry[country]
            ?: throw IllegalArgumentException("Unknown country: $country")

        val products = ProductRepo.getProductsByCountry(country)
        return products.map { p ->
            ProductDto(
                id = p.id,
                name = p.name,
                basePrice = p.basePrice,
                country = p.country,
                discounts = p.discounts,
                finalPrice = p.finalPrice(vat)
            )
        }
    }

    fun applyDiscount(productId: String, request: DiscountDto): Map<String, Any> {
        val product = ProductRepo.findProduct(productId)
            ?: throw NoSuchElementException("Product not found: $productId")

        val added = try {
            product.applyDiscount(request.discountId, request.percent)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(e.message ?: "Invalid discount percent")
        }

        return mapOf(
            "productId" to productId,
            "discountId" to request.discountId,
            "status" to if (added) "discount_applied" else "already_applied"
        )
    }
}