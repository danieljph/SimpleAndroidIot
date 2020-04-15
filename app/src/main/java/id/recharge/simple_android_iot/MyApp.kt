package id.recharge.simple_android_iot

import android.app.Application
import timber.log.Timber

/**
 * @author Daniel Joi Partogi Hutapea
 */
class MyApp : Application()
{
    companion object {
        lateinit var instance: MyApp
    }

    override fun onCreate()
    {
        super.onCreate()
        instance = this
        Timber.plant(Timber.DebugTree())
    }
}
