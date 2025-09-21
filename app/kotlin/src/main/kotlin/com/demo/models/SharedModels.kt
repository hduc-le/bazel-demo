package com.demo.models

import com.google.gson.Gson
import java.time.Instant

/**
 * Shared data models that can be used across different services in the monorepo
 */
data class User(
    val id: Long,
    val name: String,
    val email: String,
    val createdAt: String
)

data class Product(
    val id: Long,
    val name: String,
    val price: Double,
    val category: String,
    val inStock: Boolean
)

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val timestamp: String = Instant.now().toString()
)

object JsonSerializer {
    private val gson = Gson()
    
    fun <T> toJson(obj: T): String = gson.toJson(obj)
    
    fun <T> fromJson(json: String, clazz: Class<T>): T = gson.fromJson(json, clazz)
}
