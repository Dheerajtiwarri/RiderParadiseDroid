package com.droid.riderparadise

import com.droid.riderparadise.core.security.Hashing
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HashingTest {

    @Test
    fun `otp code is six digits`() {
        repeat(50) {
            val code = Hashing.newOtpCode()
            assertEquals(6, code.length)
            assertTrue(code.all { it.isDigit() })
        }
    }

    @Test
    fun `hash is deterministic for same salt and value`() {
        val salt = "fixedsalt"
        assertEquals(Hashing.hash("481523", salt), Hashing.hash("481523", salt))
    }

    @Test
    fun `hash differs for different salt`() {
        assertNotEquals(Hashing.hash("481523", "saltA"), Hashing.hash("481523", "saltB"))
    }

    @Test
    fun `wrong code does not match stored hash`() {
        val salt = Hashing.newSalt()
        val stored = Hashing.hash("481523", salt)
        assertNotEquals(stored, Hashing.hash("000000", salt))
    }

    @Test
    fun `userId is stable for same phone regardless of formatting`() {
        assertEquals(
            Hashing.userIdForPhone("415 820 4407"),
            Hashing.userIdForPhone("4158204407"),
        )
    }
}
