package com.duressvault.data

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PinHasher {
    private const val PBKDF2_ITERATIONS = 100_000
    private const val HASH_LENGTH_BITS = 256
    private const val SALT_LENGTH_BYTES = 16

    data class HashResult(val hash: ByteArray, val salt: ByteArray)

    fun hashPin(pin: CharArray): HashResult {
        val salt = ByteArray(SALT_LENGTH_BYTES).apply { SecureRandom().nextBytes(this) }
        val hash = pbkdf2(pin, salt)
        return HashResult(hash, salt)
    }

    fun verifyPin(pin: CharArray, salt: ByteArray, expectedHash: ByteArray): Boolean {
        val computedHash = pbkdf2(pin, salt)
        return constantTimeEquals(computedHash, expectedHash)
    }

    private fun pbkdf2(pin: CharArray, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(pin, salt, PBKDF2_ITERATIONS, HASH_LENGTH_BITS)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return factory.generateSecret(spec).encoded
    }

    private fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false
        var result = 0
        for (i in a.indices) result = result or (a[i].toInt() xor b[i].toInt())
        return result == 0
    }
}
