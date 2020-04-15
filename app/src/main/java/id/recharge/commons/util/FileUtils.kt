package id.recharge.commons.util

import timber.log.Timber
import java.io.File
import java.io.FileWriter

/**
 * @author Daniel Joi Partogi Hutapea
 */
object FileUtils
{
    private val TAG = FileUtils::class.java.simpleName

    fun write(file: File, data: String)
    {
        FileWriter(file).use {
            it.write(data)
        }
    }

    fun write(pathname: String, data: String)
    {
        val file = File(pathname)
        file.parentFile.mkdirs()
        FileWriter(file).use {
            it.write(data)
        }
    }

    fun delete(pathname: String): Boolean
    {
        var result = false
        try
        {
            val file = File(pathname)
            if(file.exists())
            {
                result = file.delete()
            }
        }
        catch(ex: Throwable)
        {
            Timber.e(ex, "Failed to delete file '$pathname'.")
        }
        Timber.i("Deleting '$pathname' has done. Is Deleted Successfully: $result")
        return result
    }

    fun checkFileExists(pathname: String): Boolean
    {
        var status = false
        if(pathname != "")
        {
            val newPath = File(pathname)
            status = newPath.exists()
        }
        return status
    }
}
