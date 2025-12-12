package com.example.a2faproject

import com.example.a2faproject.utils.TimeBasedOTP
import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TotpTest {

    @Test
    fun testRfc6238StandardVector() {
        // This is a standard test vector from the TOTP definition (RFC 6238).

        // 1. The Secret: "12345678901234567890" represented in Base32
        val secret = "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ"

        // 2. The Time: 59 seconds (which is Time Interval #1)
        // Logic: 59 / 30 = 1
        val timeIndex = 1L

        // 3. The Expected Result: 287082
        val expectedCode = "287082"

        // Run your function
        val actualCode = TimeBasedOTP.generateCode(secret, timeIndex)

        // Check
        assertEquals("Code generated does not match standard", expectedCode, actualCode)
    }
}