package id.recharge.iot_core.model

data class PbProvisioningResponse(
        var createdThing: Thing,
        var createdKeysAndCertificate: KeysAndCertificate
)
