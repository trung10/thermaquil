package com.tmp.thermaquil.common

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
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

    fun floatToBytes(f: Float): ArrayList<Byte> {
        val intBytes = f.toBits()
        return arrayListOf((intBytes shr 24).toByte(), (intBytes shr 16).toByte(), (intBytes shr 8).toByte(), intBytes.toByte())
    }

    fun bytesToFloat(bytes: List<Byte>): Float {
        val intBits: Int =
            bytes[0].toInt() shl 24 or ((bytes[1]
                    and 0xFF.toByte()).toInt() shl 16) or ((bytes[2]
                    and 0xFF.toByte()).toInt() shl 8) or (bytes[3]
                    and 0xFF.toByte()).toInt()
        return Float.fromBits(intBits)
    }

    fun bytesToInt(bytes: List<Byte>): Int {
        return (bytes[3].toInt() shl 24) or
                (bytes[2].toInt() and 0xff shl 16) or
                (bytes[1].toInt() and 0xff shl 8) or
                (bytes[0].toInt() and 0xff)
    }

    fun intTo4Bytes(i: Int): ArrayList<Byte> {
        return arrayListOf((i shr 0).toByte(), (i shr 8).toByte(), (i shr 16).toByte(), (i shr 24).toByte())
    }
}