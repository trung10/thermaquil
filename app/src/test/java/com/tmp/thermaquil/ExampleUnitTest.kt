package com.tmp.thermaquil

import com.tmp.thermaquil.common.Utils
import com.tmp.thermaquil.data.models.Data
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val cycle = Data.defaultTreatment.cycles[0]


        var bytearray = byteArrayOf(0x01, cycle.orderByCycle.toByte())
        bytearray = bytearray.plus(Utils.floatToBytes(cycle.hotSetPont))
        bytearray = bytearray.plus(Utils.intTo4Bytes(cycle.hotTime))
        bytearray = bytearray.plus(Utils.floatToBytes(cycle.coldSetPont))
        bytearray = bytearray.plus(Utils.intTo4Bytes(cycle.coldTime))

        bytearray.forEach {

        }


        assertEquals(4, bytearray)
    }
}