package org.example.config

import java.io.File
import java.util.Properties

object Config {
    private val properties = Properties()
    
    init {
        val localProperties = File("local.properties")
        if (localProperties.exists()) {
            properties.load(localProperties.inputStream())
        } else {
            // Try to load from resources (for testing)
            val resource = this::class.java.classLoader.getResource("local.properties")
            if (resource != null) {
                properties.load(resource.openStream())
            }
        }
    }
    
    fun getProperty(key: String): String? = properties.getProperty(key)
    
    fun getProperty(key: String, defaultValue: String): String = 
        properties.getProperty(key) ?: defaultValue
    
    fun getRequiredProperty(key: String): String = 
        getProperty(key) ?: throw IllegalStateException("Required property '$key' not found in local.properties")
}
