package org.crescent

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Test class to verify Kotlin 2.1 functionality
 */
class KotlinVersionTestTest {

    @Test
    fun testKotlinVersionIsCorrect() {
        val kotlinTest = KotlinVersionTest()
        val version = kotlinTest.getKotlinVersion()

        // Verify that we're using Kotlin 2.1.0
        assertTrue(version.startsWith("2.1"), "Expected Kotlin version 2.1.x, but got $version")
    }

    @Test
    fun testGreetingFunction() {
        val kotlinTest = KotlinVersionTest()
        val greeting = kotlinTest.greetUser("World")

        assertTrue(greeting.contains("Hello World"))
        assertTrue(greeting.contains("Kotlin 2.1"))
    }
}
