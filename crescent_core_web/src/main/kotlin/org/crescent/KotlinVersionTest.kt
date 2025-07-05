package org.crescent

/**
 * Simple Kotlin class to verify Kotlin 2.1 compilation
 */
class KotlinVersionTest {
    fun getKotlinVersion(): String {
        return KotlinVersion.CURRENT.toString()
    }
    
    fun greetUser(name: String): String {
        return "Hello $name from Kotlin ${KotlinVersion.CURRENT}!"
    }
}