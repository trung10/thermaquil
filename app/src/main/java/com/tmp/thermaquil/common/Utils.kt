package com.tmp.thermaquil.common

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.nio.ByteBuffer
import kotlin.experimental.and


object Utils{

    fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    fun validatePassword(password: String): Boolean {
        if (password.length < 8) {
            return false
        }

        var hasUpperCase = false;
        var hasDigitCharacter = false;
        var hasLowerCase = false;

        var count = 0
        for (i in password.indices) {
            val character = password[i]
            when {
                Character.isDigit(character) -> {
                    hasDigitCharacter = true;
                    count++;
                }
                Character.isUpperCase(character) -> {
                    hasUpperCase = true;
                    count++;
                }
                Character.isLowerCase(character) -> {
                    hasLowerCase = true;
                    count++;
                }
            }
        }

        val hasSpecialCharacter = count != password.length

        if (!hasUpperCase || !hasDigitCharacter || !hasLowerCase || !hasSpecialCharacter) {
            return false
        }

        return true
    }

    fun dpToPixel(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    fun hideSoftKeyboard(view: View,context: Context) {
        val imm: InputMethodManager? =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun floatToBytes(f: Float): ByteArray {
        val bits = f.toBits()
        return byteArrayOf(
            (bits shr 24).toByte(),
            (bits shr 16).toByte(),
            (bits shr 8).toByte(),
            bits.toByte()
        )
    }

    fun bytesToFloat(byteArray: ByteArray): Float{
        val buffer = ByteBuffer.wrap(byteArray)
        return buffer.float
    }

    fun intTo4Bytes(data: Int, size: Int = 4): ByteArray = ByteArray(size) { i ->
        (data shr (i*8)).toByte()
    }

    fun bytesToInt(buffer: ByteArray, offset: Int = 0): Int {
        return (buffer[offset + 3].toInt() shl 24) or
                (buffer[offset + 2].toInt() and 0xff shl 16) or
                (buffer[offset + 1].toInt() and 0xff shl 8) or
                (buffer[offset + 0].toInt() and 0xff)
    }

    fun writeFileOnInternalStorage(mcoContext: Context, sFileName: String?, bytes: ArrayList<Byte>) {
        val dir = File(mcoContext.filesDir, "mydir")
        if (!dir.exists()) {
            dir.mkdir()
        }
        try {
            val gpxfile = File(dir, sFileName)
            val writer = FileOutputStream(gpxfile)
            writer.write(bytes.toByteArray())
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}