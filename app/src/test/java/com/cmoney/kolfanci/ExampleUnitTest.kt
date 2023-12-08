package com.cmoney.kolfanci

import com.cmoney.kolfanci.utils.Utils
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun encrypt_isCorrect() {
        val input = 12345
        val key = 1357

        val encryptResult = Utils.encryptInviteCode(
            input = input,
            key = key
        )

        println("encryptResult:$encryptResult")

        val decryptOpt = Utils.decryptInviteCode(
            input = encryptResult,
            key = key
        )

        assertEquals(input, decryptOpt)
    }
}