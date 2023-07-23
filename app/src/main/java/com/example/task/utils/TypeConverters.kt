package com.example.task.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import java.io.ByteArrayOutputStream
import java.util.Date
import java.util.Locale

fun fromBitmap(bitmap: Bitmap): ByteArray {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    return outputStream.toByteArray()
}

fun toBitmap(byteArray: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}

fun Long.toFormattedDateTime(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    val date = Date(this)
    return dateFormat.format(date)
}