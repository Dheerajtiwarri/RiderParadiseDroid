package com.droid.riderparadise.core.security

import java.security.MessageDigest
import java.security.SecureRandom

/** SHA-256 + per-record salt helpers for storing OTP codes at rest, and a stable phone→id hash. */
object Hashing {
    private val random = SecureRandom()

    fun newSalt(): String {
        val bytes = ByteArray(16)
        random.nextBytes(bytes)
        return bytes.toHex()
    }

    fun hash(value: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest((salt + value).toByteArray()).toHex()
    }

    /** Deterministic 6-digit code via SecureRandom. */
    fun newOtpCode(): String = (random.nextInt(900_000) + 100_000).toString()

    /** POC user id derived from the phone number (no real auth backend). */
    fun userIdForPhone(phone: String): String =
        "u_" + hash(phone.filter { it.isDigit() }, "rp_phone").take(20)

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
}
