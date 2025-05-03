package com.example.gym_application
import com.example.gym_application.utils.ValidationUtils
import org.junit.Assert.*
import org.junit.Test


class RegisterPageUnitTest {

    @Test
    fun `valid email should return true`() {
        assertTrue(ValidationUtils.isValidEmail("test@gmail.com"))
    }

    @Test
    fun `invalid email without TLD should return false`() {
        assertFalse(ValidationUtils.isValidEmail(""))
        assertFalse(ValidationUtils.isValidEmail("test@gmail"))
        assertFalse(ValidationUtils.isValidEmail("test@gmail."))
    }

    @Test
    fun `valid password should return true`() {
        assertTrue(ValidationUtils.isValidPassword("StrongPass123"))
    }


    @Test fun `empty firstname should return false`() {
        assertFalse(ValidationUtils.isValidFirstname(""))
    }

    @Test fun `short firstname should return false`() {
        assertFalse(ValidationUtils.isValidFirstname("A"))
    }

    @Test fun `empty lastname should return false`() {
        assertFalse(ValidationUtils.isValidLastName(""))
    }

    @Test fun `short last should return false`() {
        assertFalse(ValidationUtils.isValidLastName("A"))
    }

    @Test
    fun `password too short should return false`() {
        assertFalse(ValidationUtils.isValidPassword("123"))
        assertFalse(ValidationUtils.isValidPassword("p"))
        assertFalse(ValidationUtils.isValidPassword(""))

    }

    @Test
    fun `valid day should return true`() {
        assertTrue(ValidationUtils.isValidDay("15"))
    }

    @Test
    fun `invalid day above range should return false`() {
        assertFalse(ValidationUtils.isValidDay("32"))  // Day must be 1-31
    }

    @Test
    fun `valid phone number should return true`() {
        assertTrue(ValidationUtils.isValidPhoneNumber("+12345678901"))
    }

    @Test
    fun `invalid phone number should return false`() {
        assertFalse(ValidationUtils.isValidPhoneNumber("12345"))
    }

    @Test
    fun `matching passwords should return true`() {
        assertTrue(ValidationUtils.doPasswordsMatch("Password123", "Password123"))
    }

    @Test
    fun `non-matching passwords should return false`() {
        assertFalse(ValidationUtils.doPasswordsMatch("Password123", "Password456"))
    }
}