package com.android.stickerpocket

data class Type(
    val typeId: String? = null,
    val typeName: String? = null
) {
    override fun toString(): String{
        return typeName ?: "NA"
    }
}
