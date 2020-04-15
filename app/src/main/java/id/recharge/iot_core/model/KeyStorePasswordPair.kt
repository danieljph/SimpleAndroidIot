package id.recharge.iot_core.model

import java.security.KeyStore

data class KeyStorePasswordPair(
    val keyStore: KeyStore,
    val keyPassword: String
)
