package org.example

import io.ktor.server.application.Application
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.net.ServerSocket

fun findAvailablePort(startPort: Int = 8080, endPort: Int = 9000): Int {
    for (port in startPort..endPort) {
        try {
            ServerSocket(port).use { socket ->
                socket.reuseAddress = true
                return port
            }
        } catch (e: Exception) {
            println("Error finding free port: ${e.message}")
        }
    }
    throw IllegalStateException("No available port found between $startPort and $endPort")
}

fun main() {
    val port = findAvailablePort()
    println("Starting server on port $port")
    println("Open your browser at: http://localhost:$port")
    
    embeddedServer(Netty, port = port, module = Application::module).start(wait = true)
}