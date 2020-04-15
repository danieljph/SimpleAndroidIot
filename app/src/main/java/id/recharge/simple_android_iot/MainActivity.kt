package id.recharge.simple_android_iot

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.services.iot.client.AWSIotConnectionStatus
import com.amazonaws.services.iot.client.AWSIotMqttClient
import id.recharge.commons.util.FileUtils
import id.recharge.iot_core.model.RcDevice
import id.recharge.iot_core.util.KeyStoreUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

/**
 * @author Daniel Joi Partogi Hutapea
 */
class MainActivity : AppCompatActivity()
{
    companion object {
        private const val AWS_CONNECT_TIMEOUT_IN_MILLISECONDS = 10_000L
    }

    private val thingName = "TestSimpleIot"
    private val sslFolderPathname = "${MyApp.instance.applicationContext.filesDir.absolutePath}/LaidianClient/AWS/ssl"
    private val certificatePathname = "$sslFolderPathname/$thingName-certificate.pem.crt"
    private val privateKeyPathname = "$sslFolderPathname/$thingName-private.pem.key"
    private var client: AWSIotMqttClient? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnConnect.setOnClickListener {
            if(btnConnect.text == "Connect")
            {
                enableButton(false)
                connect {
                    enableButton(true)

                    btnConnect.text = if (it) "Disconnect" else "Connect"
                }
            }
            else
            {
                enableButton(false)
                disconnect {
                    enableButton(true)
                    btnConnect.text = "Connect"
                }
            }
        }

        btnReChargeNewProduction.setOnClickListener { tvEndpoint.text = "a3bh7jjcvln4pn-ats.iot.us-east-1.amazonaws.com" }
        btnReChargeNewStaging.setOnClickListener { tvEndpoint.text = "a23x7roz31nypq-ats.iot.us-east-1.amazonaws.com" }
        btnReChargeOldDevelopment.setOnClickListener { tvEndpoint.text = "a200oqh54iecas-ats.iot.us-east-1.amazonaws.com" }
        btnReChargeOldProduction.setOnClickListener { tvEndpoint.text = "a200oqh54iecas-ats.iot.ap-southeast-1.amazonaws.com" }
        btnClearLog.setOnClickListener { tvInfo.text = "Log Info:\n" }
    }

    private fun enableButton(enable: Boolean)
    {
        btnConnect.isEnabled = enable
        btnReChargeNewProduction.isEnabled = enable
        btnReChargeNewStaging.isEnabled = enable
        btnReChargeOldDevelopment.isEnabled = enable
        btnReChargeOldProduction.isEnabled = enable
        btnClearLog.isEnabled = enable
    }

    private fun connect(onFinished: (success: Boolean)->Unit)
    {
        Thread {
            var success = false
            val endpoint = tvEndpoint.text.toString()

            try
            {
                logInfo("Connecting to AWS IOT using endpoint '$endpoint'.")
                createPrivateKeyAndCertificate()
                val keyStorePasswordPair = KeyStoreUtil.getKeyStorePasswordPair(certificatePathname, privateKeyPathname)

                val rcDevice = RcDevice(thingName)
                rcDevice.reportInterval = 5_000

                client = AWSIotMqttClient(endpoint, thingName, keyStorePasswordPair.keyStore, keyStorePasswordPair.keyPassword).apply {
                    attach(rcDevice)
                    connect(AWS_CONNECT_TIMEOUT_IN_MILLISECONDS)
                }
                logInfo("Connecting to AWS IOT using endpoint '$endpoint' success.")
                success = true
            }
            catch(ex: Exception)
            {
                logInfo("Connecting to AWS IOT using endpoint '$endpoint' failed.")
                tvInfo.append(Log.getStackTraceString(ex))
            }
            runOnUiThread { onFinished(success) }
        }.start()
    }

    private fun disconnect(onFinished: ()->Unit)
    {
        Thread {
            try
            {
                if(client?.connectionStatus == AWSIotConnectionStatus.CONNECTED)
                {
                    logInfo("Disconnecting from AWS IOT.")
                    client?.disconnect()
                    logInfo("Disconnecting from AWS IOT success.")
                }
            }
            catch(ex: Exception)
            {
                logInfo("Disconnecting from AWS IOT failed.")
                tvInfo.append(Log.getStackTraceString(ex))
            }
            runOnUiThread { onFinished() }
        }.start()
    }

    private fun createPrivateKeyAndCertificate()
    {
        FileUtils.delete(certificatePathname)
        val certificatePem = "-----BEGIN CERTIFICATE-----\nMIIDWTCCAkGgAwIBAgIUUvmEj9SOWy4s9m1KgohGMk3D5okwDQYJKoZIhvcNAQEL\nBQAwTTFLMEkGA1UECwxCQW1hem9uIFdlYiBTZXJ2aWNlcyBPPUFtYXpvbi5jb20g\nSW5jLiBMPVNlYXR0bGUgU1Q9V2FzaGluZ3RvbiBDPVVTMB4XDTIwMDQxMzA0NTc1\nNloXDTQ5MTIzMTIzNTk1OVowHjEcMBoGA1UEAwwTQVdTIElvVCBDZXJ0aWZpY2F0\nZTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAL8+ZvTLvFhVmpCaY9+9\nFCmTBxQ0XXxoHWs2fyIUmpGAeSc9UBV4HKVBkVKm+l0RimGY+vo42Fw09AfBloKU\nntj1b8BTlKsH0aMZMClxhtWapcD3f677AwcQ2/W83D8C4jKaDfttANoZjMCePRZj\n4fi0Y2Yb8bdmClcRyZYbGDvPQcUNNlxoy8AMhKWQ0GzEdtdkNs7OPfc1mmzUVE3L\nCBX1nPo50txha1AfozG8LNzg6M5JKaILrcMNJnLk6UPInD96g8lxjAHouQW1bO0r\nVpYBIGgaZb/QNqw/ql9kHRQW//t7KQ70OId9/906xVPdy9/ge/YRqgTb6GQFbjFd\n2KsCAwEAAaNgMF4wHwYDVR0jBBgwFoAUQ/PjiETfC5mfkQhB8rfheq83gGAwHQYD\nVR0OBBYEFEk2levCnEUA0R97QBjavTMU5xIlMAwGA1UdEwEB/wQCMAAwDgYDVR0P\nAQH/BAQDAgeAMA0GCSqGSIb3DQEBCwUAA4IBAQA+GnZjWqwnPOPWOTpsILMpcFus\nqYRGQLiRJQlCGxd69U+3hXKj1AyGM514z3LAAsqWXWhoHVimTrW+inqVZlWN8yOt\noPcH/kjYiX076Ob3N/KpefUfhuleTCt91BNWz/VDmCobVJO7T0MHhZy+xjSyAcsb\nJE9IqsIB3/iXh7YOQXcDJoA8FO3sVVsbAlibDFBTfB90W6recsIuD7R48bgRpm4/\nzvmsFSUOlVukNJdEBD5eKGuHZCprO3xLh5vEm4E8+s26FjWH/ZqSfAFhE6tbmxMr\nVwxo6EsvM705N0OZnduRAeXpz3h1gom2HbfFNX7Stw4rB4bsbKAWBASiCLka\n-----END CERTIFICATE-----\n"
        FileUtils.write(certificatePathname, certificatePem)

        FileUtils.delete(privateKeyPathname)
        val privateKeyPem = "-----BEGIN RSA PRIVATE KEY-----\nMIIEpAIBAAKCAQEAvz5m9Mu8WFWakJpj370UKZMHFDRdfGgdazZ/IhSakYB5Jz1Q\nFXgcpUGRUqb6XRGKYZj6+jjYXDT0B8GWgpSe2PVvwFOUqwfRoxkwKXGG1ZqlwPd/\nrvsDBxDb9bzcPwLiMpoN+20A2hmMwJ49FmPh+LRjZhvxt2YKVxHJlhsYO89BxQ02\nXGjLwAyEpZDQbMR212Q2zs499zWabNRUTcsIFfWc+jnS3GFrUB+jMbws3ODozkkp\nogutww0mcuTpQ8icP3qDyXGMAei5BbVs7StWlgEgaBplv9A2rD+qX2QdFBb/+3sp\nDvQ4h33/3TrFU93L3+B79hGqBNvoZAVuMV3YqwIDAQABAoIBAQCyDQ8d6nE4bLqy\n1osVy5vx+QkmLnq7UNPS9bH3sOcXgF1LwzHES0egNCNwqDWAYMxNgXhpbnNBpatt\nbTcL7ALYS+n3TdmkQmtTRigo7aFzTb/0oHkoGFsBQRkI+QS1RaIDKprJ8vpQFGmP\nC8QkDGpeetPwRSQOc9o8pjrgnek6BGhZhHI18u0X+7EyNJcBeBJ8jIP2eGXUx4AQ\nDba55JzPDESo/Q6y4wMV+c1ynM1hCdfYnmiDYTD7M99h7otuIha8EFEzjnNb6ZZK\nuHt5llDNx5v1i5Du9pQ7a7QMNcFigFn3zf4NZHfLogNbrxEke1denoAXygkD0z4F\n0UJqVyABAoGBAN8MxgDQP4c1r9atwEnqDWwLQLhXxZot/oYW5iiZipMArRKq8uJr\nH1zYfM+UoUccfYlfcE7iuiUAUMy6JYvR699IOCCjSJXk4UJ7K3iJQH/fiBL1mmDY\nXh//tSDd053yl5YPKs/owj+Su61KE663ruTyyOuN1EHIlalnsQ8+mh6rAoGBANt+\ny3kajkkX1cbqZdnTtg1czs60zv2eIQHGrzU8+0B4ssIBZjCEpsKKCu5HgUVJViT8\nbnLjy+TPmrUg11dqwcPau6tDkHr0IFNQPd7VOaOkroOkNjdXdDkrnk8jEk1riOTW\nXM9RNwTfCPKOG5a/CdSGOW/BclXzlqBaakmvwy4BAoGBAIqWVTCMM+y+3tJ0gtVq\nJ4zKMRxY7N4vVcXa1IbFX+SWtvKyZNdp18LIgEQ6BTs56IZCVnWtShtTZuDNiCan\n9/Zz2OvuiFsaKxwzi38JSJorOVEOaS1jQph/OPuC2Ml6wdTq957W8FqvwQyayj/0\nwSZf85boOMnm55aqZskiDIChAoGAXMCR3xUXt095KNt4Oro6Hh9vzO2e4pFlxUhe\nVZL1YWMftte8hRgpF+AZimNw/wRkgLCjQ617Ra9s4smD9g7I3qiZ0V91uF9dIZew\n7W8RMlOu4zYLKwurs1T5Stu5Kjoc2Qa0pW7eXxKYx1bB1cvYrZQixVTrqeKLbiuG\n6qh5RAECgYAX8JtSHBTryGsIov87ApYBh9WKE1VBmTwOxUjXph5Fpbj9Jf6XTSeV\n41bY3vgw0rjQTrXtwoRVD9yi5XKlKxls8pAAKeccyT7b35y4OK53cMtSJtbCNrbs\n7LGqE9SnWCnzUY/kylBm07i5qXUa91pivMiLCB+US+5upg2q4NAZgw==\n-----END RSA PRIVATE KEY-----\n"
        FileUtils.write(privateKeyPathname, privateKeyPem)
    }

    private fun logInfo(msg: String)
    {
        tvInfo.append(msg+'\n')
    }
}
