package id.recharge.iot_core

/**
 * @author Daniel Joi Partogi Hutapea
 */
data class State(
    var desired: TerminalInfo? = null,
    var reported: TerminalInfo? = null
)
