package com.newpaper.somewhere.core.model.data

data class MyColor(
    val color: Int = 0xFF493cfa.toInt(),
    val onColor: Int = 0xffffffff.toInt()
){
    override fun toString(): String{
        return "$color/$onColor"
    }
}