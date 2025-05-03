package com.example.gym_application

import com.example.gym_application.utils.ValidationClassCreation
import org.junit.Assert.*
import org.junit.Test

class ClassCreationUnitTest {

    @Test
    fun `valid class title should return true`() {
        assertTrue(ValidationClassCreation.isValidClassTitle("Yoga"))
    }

    @Test
    fun `short class title should return false`() {
        assertFalse(ValidationClassCreation.isValidClassTitle("Yo"))
    }

    @Test
    fun `class title with digits should return false`() {
        assertFalse(ValidationClassCreation.isValidClassTitle("Yoga1"))
    }

    fun `valid class description should return true`() {
        assertTrue(ValidationClassCreation.isValidClassDescription("A great class for all levels."))
    }

    @Test
    fun `empty class description should return false`() {
        assertFalse(ValidationClassCreation.isValidClassDescription(""))
    }

    @Test
    fun `too long class description should return false`() {
        val longDescription = (1..121).joinToString(" ") { "word" } // 121 words
        assertFalse(ValidationClassCreation.isValidClassDescription(longDescription))
    }

    fun `valid class color should return true`() {
        assertTrue(ValidationClassCreation.isValidClassColor("Red"))
    }

    @Test
    fun `empty class color should return false`() {
        assertFalse(ValidationClassCreation.isValidClassColor(""))
    }

    fun `null class color should return false`() {
        assertFalse(ValidationClassCreation.isValidClassColor(null))
    }

    // --- Capacity ---
    @Test
    fun `valid class capacity should return true`() {
        assertTrue(ValidationClassCreation.isValidCapacity("10"))
    }

    fun `zero class capacity should return false`() {
        assertFalse(ValidationClassCreation.isValidCapacity("0"))
    }

    @Test
    fun `over limit class capacity should return false`() {
        assertFalse(ValidationClassCreation.isValidCapacity("25"))
    }

    @Test
    fun `non-numeric class capacity should return false`() {
        assertFalse(ValidationClassCreation.isValidCapacity("abc"))
    }


    @Test
    fun `valid availability option should return true`() {
        assertTrue(ValidationClassCreation.isValidClassColor("Members"))
    }

    @Test
    fun `empty availability option should return false`() {
        assertFalse(ValidationClassCreation.isValidClassColor(""))
    }

    @Test
    fun `null availability option should return false`() {
        assertFalse(ValidationClassCreation.isValidClassColor(null))
    }

}