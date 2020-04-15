package id.recharge.iot_core.model

import com.google.gson.annotations.SerializedName

data class KeyPair(
    @SerializedName("PublicKey") var publicKey: String,
    @SerializedName("PrivateKey") var privateKey: String
)
