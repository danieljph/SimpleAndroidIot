package id.recharge.iot_core.model

import android.os.Build
import com.amazonaws.services.iot.client.AWSIotDevice
import com.amazonaws.services.iot.client.AWSIotDeviceProperty
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Daniel Joi Partogi Hutapea
 */
@Suppress("SuspiciousVarProperty")
class RcDevice(thingName: String) : AWSIotDevice(thingName)
{
    companion object {
        private val ISO_8601_SDF = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }
    }

    override fun onDeviceReport(): String {
        Timber.i("AwsIotCore preparing device report...")
        val onDeviceReportResult =  super.onDeviceReport()
        Timber.i("AwsIotCore preparing device report has done.")
        return onDeviceReportResult
    }

    @field:AWSIotDeviceProperty var lastUpdateAt: String? = null
        get() = ISO_8601_SDF.format(Date())

    @field:AWSIotDeviceProperty var id = thingName

    @field:AWSIotDeviceProperty var manufacturer = Build.MANUFACTURER
    @field:AWSIotDeviceProperty var model = Build.MODEL
}
