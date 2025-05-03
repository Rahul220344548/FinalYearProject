package com.example.gym_application
import com.example.gym_application.utils.ValidationClassCreation
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ScheduleCreationUnitTest {

    @Test
    fun `valid class option name should return true`() {
        assertTrue(ValidationClassCreation.isValidSelectClassOptionName("Cardio"))
    }

    @Test
    fun `empty class option name should return false`() {
        assertFalse(ValidationClassCreation.isValidSelectClassOptionName(""))
    }

    @Test
    fun `valid room selection should return true`() {
        assertTrue(ValidationClassCreation.isValidSelectRoom("Room A"))
    }

    @Test
    fun `empty room selection should return false`() {
        assertFalse(ValidationClassCreation.isValidSelectRoom(""))
    }

    @Test
    fun `valid instructor selection should return true`() {
        assertTrue(ValidationClassCreation.isValidSelectInstructor("John"))
    }

    @Test
    fun `empty instructor selection should return false`() {
        assertFalse(ValidationClassCreation.isValidSelectInstructor(""))
    }

    @Test
    fun `valid 30 minute time slot should return true`() {
        assertTrue(ValidationClassCreation.isValidTime("10:00", "10:30"))
    }

    @Test
    fun `invalid time duration should return false`() {
        assertFalse(ValidationClassCreation.isValidTime("10:00", "10:20"))
    }

    @Test
    fun `end time before start time should return false`() {
        assertFalse(ValidationClassCreation.isValidTime("12:00", "11:30"))
    }

    @Test
    fun `non-empty schedule date should return true`() {
        assertTrue(ValidationClassCreation.isValidScheduledDate("10/05/2025"))
    }

    @Test
    fun `empty schedule date should return false`() {
        assertFalse(ValidationClassCreation.isValidScheduledDate(""))
    }

    @Test
    fun `future schedule time should return true`() {
        val futureDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val time = LocalTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("HH:mm"))
        assertTrue(ValidationClassCreation.isScheduleTimeInFuture(futureDate, time))
    }

    @Test
    fun `past schedule time should return false`() {
        val pastDate = "01/01/2020"
        val time = "08:00"
        assertFalse(ValidationClassCreation.isScheduleTimeInFuture(pastDate, time))
    }
}