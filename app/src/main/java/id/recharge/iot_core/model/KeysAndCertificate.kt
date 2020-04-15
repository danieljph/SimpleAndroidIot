package id.recharge.iot_core.model

data class KeysAndCertificate(
    var certificateId: String,
    var certificateArn: String,
    var certificatePem: String,
    var keyPair: KeyPair
)
