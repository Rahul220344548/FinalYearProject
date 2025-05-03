package com.example.gym_application

import com.example.gym_application.utils.ValidationClassCreation
import org.junit.Assert.*
import org.junit.Test

class MembershipCreationUnitTest {

    @Test fun `valid membership title should return true`() {
        assertTrue(ValidationClassCreation.isValidClassTitle("Premium"))
    }

    @Test fun `short membership title should return false`() {
        assertFalse(ValidationClassCreation.isValidClassTitle("A"))
    }

    @Test fun `title with digits should return false`() {
        assertFalse(ValidationClassCreation.isValidClassTitle("Gold1"))
    }

    @Test fun `valid membership duration should return true`() {
        assertTrue(ValidationClassCreation.isValidClassColor("3 Months"))
    }

    @Test fun `empty duration should return false`() {
        assertFalse(ValidationClassCreation.isValidClassColor(""))
    }

    @Test fun `null duration should return false`() {
        assertFalse(ValidationClassCreation.isValidClassColor(null))
    }

    @Test fun `valid membership price should return true`() {
        assertTrue(ValidationClassCreation.isValidMembershipPrice("30"))
    }

    @Test fun `non-numeric price should return false`() {
        assertFalse(ValidationClassCreation.isValidMembershipPrice("abc"))
    }

    @Test fun `zero price should return false`() {
        assertFalse(ValidationClassCreation.isValidMembershipPrice("0"))
    }

    @Test fun `negative price should return false`() {
        assertFalse(ValidationClassCreation.isValidMembershipPrice("-50"))
    }
}