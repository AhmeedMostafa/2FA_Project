package com.example.a2faproject.utils

import org.apache.commons.codec.binary.Base32
import java.nio.ByteBuffer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object TimeBasedOTP {

    // 1. Calculate the number of "intervals" of 30 seconds
    // Equation: T = (Current Time) / 30
    fun generateCurrentCode(secret: String): String {
        val timeIndex = System.currentTimeMillis() / 1000 / 30
        return generateCode(secret, timeIndex)
    }

    fun generateCode(secret: String, timeIndex: Long): String {
        try {
            val cleanSecret = secret.trim().replace(" ", "").uppercase()

            // Decode the Base32 Secret Key
            val base32 = Base32()
            val decodedKey = base32.decode(cleanSecret)

            // Convert timeIndex to a byte array
            val data = ByteBuffer.allocate(8).putLong(timeIndex).array()

            // Calculate HMAC-SHA1
            val mac = Mac.getInstance("HmacSHA1")
            mac.init(SecretKeySpec(decodedKey, "HmacSHA1"))
            val hash = mac.doFinal(data)

            // Dynamic Truncation (The standard TOTP logic)
            val offset = hash[hash.size - 1].toInt() and 0xF
            val binary = ((hash[offset].toInt() and 0x7f) shl 24) or
                    ((hash[offset + 1].toInt() and 0xff) shl 16) or
                    ((hash[offset + 2].toInt() and 0xff) shl 8) or
                    (hash[offset + 3].toInt() and 0xff)

            // Get the last 6 digits
            val otp = binary % 1000000

            // Format as "000000" (pad with zeros if needed)
            return String.format("%06d", otp)

        } catch (e: Exception) {
            e.printStackTrace()
            return "000000" // Error case
        }
    }
}