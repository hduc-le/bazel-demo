package com.demo.service

import com.demo.models.*
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.time.Instant
import java.util.concurrent.Executors

fun main() {
    val server = SimpleKotlinServer(8080)
    println("Starting Kotlin service on port 8080...")
    server.start()
}

class SimpleKotlinServer(private val port: Int) {
    private val gson = Gson()
    private val executor = Executors.newFixedThreadPool(10)
    
    fun start() {
        val serverSocket = ServerSocket(port)
        println("Kotlin service listening on port $port")
        
        while (true) {
            val clientSocket = serverSocket.accept()
            executor.submit {
                handleRequest(clientSocket)
            }
        }
    }
    
    private fun handleRequest(socket: Socket) {
        try {
            val input = socket.getInputStream().bufferedReader()
            val output = socket.getOutputStream().bufferedWriter()
            
            // Read the HTTP request
            val requestLine = input.readLine()
            val path = requestLine?.split(" ")?.get(1) ?: "/"
            
            // Skip headers
            while (input.readLine()?.isNotEmpty() == true) {
                // Skip headers
            }
            
            val responseData = when (path) {
                "/" -> ApiResponse(
                    success = true,
                    data = mapOf("message" to "Hello from Kotlin service built with Bazel!"),
                    message = "Kotlin service is running"
                )
                
                "/health" -> ApiResponse(
                    success = true,
                    data = mapOf(
                        "status" to "healthy",
                        "service" to "kotlin-service",
                        "version" to "1.0.0"
                    )
                )
                
                "/products" -> {
                    val products = listOf(
                        Product(1, "Laptop", 999.99, "Electronics", true),
                        Product(2, "Book", 19.99, "Education", true),
                        Product(3, "Coffee Mug", 12.50, "Kitchen", false)
                    )
                    ApiResponse(
                        success = true,
                        data = products
                    )
                }
                
                "/call-python" -> {
                    try {
                        val pythonResponse = callPythonService()
                        ApiResponse(
                            success = true,
                            data = mapOf(
                                "kotlin_message" to "Hello from Kotlin!",
                                "python_response" to pythonResponse
                            ),
                            message = "Successfully called Python service from Kotlin"
                        )
                    } catch (e: Exception) {
                        ApiResponse<Nothing>(
                            success = false,
                            message = "Failed to call Python service: ${e.message}"
                        )
                    }
                }
                
                else -> {
                    // Handle user ID path
                    if (path.startsWith("/users/")) {
                        val userIdStr = path.removePrefix("/users/")
                        val userId = userIdStr.toLongOrNull()
                        
                        if (userId == null) {
                            ApiResponse<Nothing>(
                                success = false,
                                message = "Invalid user ID"
                            )
                        } else {
                            val user = User(
                                id = userId,
                                name = "John Doe $userId",
                                email = "john.doe$userId@example.com",
                                createdAt = Instant.now().toString()
                            )
                            ApiResponse(
                                success = true,
                                data = user
                            )
                        }
                    } else {
                        ApiResponse<Nothing>(
                            success = false,
                            message = "Endpoint not found"
                        )
                    }
                }
            }
            
            val jsonResponse = gson.toJson(responseData)
            
            // Send HTTP response
            output.write("HTTP/1.1 200 OK\r\n")
            output.write("Content-Type: application/json\r\n")
            output.write("Content-Length: ${jsonResponse.length}\r\n")
            output.write("Access-Control-Allow-Origin: *\r\n")
            output.write("\r\n")
            output.write(jsonResponse)
            output.flush()
            
        } catch (e: Exception) {
            println("Error handling request: ${e.message}")
        } finally {
            socket.close()
        }
    }
    
    private fun callPythonService(): String {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://localhost:8000/")
            .build()
        
        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            response.body?.string() ?: "Empty response"
        }
    }
}