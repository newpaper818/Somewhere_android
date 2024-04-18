package com.newpaper.somewhere.core.model.enums

enum class ProviderId(
    val id: String,
    val providerName: String
) {
    GOOGLE("google.com", "Google"),
    APPLE("apple.com", "Apple"),
}

fun getProviderIdFromString(value: String): ProviderId?{
    return when(value){
        ProviderId.GOOGLE.id -> ProviderId.GOOGLE
        ProviderId.GOOGLE.providerName -> ProviderId.GOOGLE

        ProviderId.APPLE.id -> ProviderId.APPLE
        ProviderId.APPLE.providerName -> ProviderId.APPLE

        else -> null
    }
}
