package xyz.dvnlabs.approval

import org.junit.Test

import org.junit.Assert.*
import xyz.dvnlabs.approval.core.util.extractNumber

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testExtractNumber(){
        val value = "N00098"
        val extracted = value.extractNumber()
        assert(extracted == 98)
    }
}