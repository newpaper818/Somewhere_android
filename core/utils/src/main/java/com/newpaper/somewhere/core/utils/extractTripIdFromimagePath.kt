package com.newpaper.somewhere.core.utils

fun extractTripIdFromImagePath(
    imagePath: String
): Int?{
    return try {
        val parts = imagePath.split("_")
        parts[0].toInt()
    }
    catch (e: NumberFormatException){
        null
    }
}