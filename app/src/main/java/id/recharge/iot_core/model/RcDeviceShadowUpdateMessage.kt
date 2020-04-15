package id.recharge.iot_core.model

import com.amazonaws.services.iot.client.AWSIotMessage

enum class ShadowUpdateType(val path: String) { ACCEPTED("accepted"), DELTA("delta") }

data class RcDeviceShadowUpdateMessage(
    var shadowUpdateType: ShadowUpdateType,
    var awsIotMessage: AWSIotMessage
)
