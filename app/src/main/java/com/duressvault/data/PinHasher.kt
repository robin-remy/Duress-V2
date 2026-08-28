package com.duressvault.data

import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2Mode
import java.security.SecureRandom

object PinHasher {
    private const val MEMORY_KiB = 64 * 1024
    private const val ITERATIONS = 5
    private const val PARALLELISM = 1
    private const val HASH_LENGTH = 32
    private const val SALT_LENGTH = 16

    private val argon2 = Argon2Kt()

    data class HashResult(val hash: ByteArray, val salt: ByteArray)

    fun hashPin(pin: CharArray): HashResult {
        val salt = ByteArray(SALT_LENGTH).apply { SecureRandom().nextBytes(this) }
        val hash = argon2.hash(
            mode = Argon2Mode.ARGON2_ID,
            password = pin.concatToString().toByteArray(Charsets.UTF_8),
            salt = salt,
            tCostInIterations = ITERATIONS,
            mCostInKibibyte = MEMORY_KiB,
            parallelism = PARALLELISM,
            hashLengthInBytes = HASH_LENGTH
        ).rawHash
        return HashResult(hash, salt)
    }

    fun verifyPin(pin: CharArray, salt: ByteArray, expectedHash: ByteArray): Boolean {
        val computedHash = argon2.hash(
            mode = Argon2Mode.ARGON2_ID,
            password = pin.concatToString().toByteArray(Charsets.UTF_8),
            salt = salt,
            tCostInIterations = ITERATIONS,
            mCostInKibibyte = MEMORY_KiB,
            parallelism = PARALLELISM,
            hashLengthInBytes = HASH_LENGTH
        ).rawHash
        return constantTimeEquals(computedHash, expectedHash)
    }

    private fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false
        var result = 0
        for (i in a.indices) result = result or (a[i].toInt() xor b[i].toInt())
        return result == 0
    }
}
