package id.recharge.iot_core.util

import id.recharge.iot_core.model.KeyStorePasswordPair
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.math.BigInteger
import java.security.KeyStore
import java.security.PrivateKey
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.CertificateFactory

/**
 * @author Daniel Joi Partogi Hutapea
 */
object KeyStoreUtil
{
    fun isCertificateAndPrivateKeyFilesExist(certificatePathname: String, privateKeyPathname: String): Boolean
    {
        val isCertificateFileExist = File(certificatePathname).exists()
        val isPrivateKeyFileExist = File(privateKeyPathname).exists()
        Timber.i("Is Certificate file exist: $isCertificateFileExist")
        Timber.i("Is Private Key file exist: $isPrivateKeyFileExist")
        return isCertificateFileExist && isPrivateKeyFileExist
    }

    fun getKeyStorePasswordPair(certificatePathname: String, privateKeyPathname: String, keyAlgorithm: String? = null): KeyStorePasswordPair
    {
        val privateKey = loadPrivateKeyFromFile(privateKeyPathname, keyAlgorithm)
        val certificates = loadCertificatesFromFile(certificatePathname)
        return getKeyStorePasswordPair(certificates, privateKey)
    }

    private fun getKeyStorePasswordPair(certificates: List<Certificate?>, privateKey: PrivateKey): KeyStorePasswordPair
    {
        val keyStore: KeyStore
        val keyPassword: String

        try
        {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null)

            // Randomly generated key password for the key in the KeyStore
            keyPassword = BigInteger(128, SecureRandom()).toString(32)

            val certChain = certificates.toTypedArray()
            keyStore.setKeyEntry("alias", privateKey, keyPassword.toCharArray(), certChain)

            return KeyStorePasswordPair(keyStore, keyPassword)
        }
        catch(ex: Exception)
        {
            throw RuntimeException("Failed to create KeyStore.")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun loadCertificatesFromFile(filename: String): List<Certificate?>
    {
        val file = File(filename)

        if(!file.exists())
        {
            throw RuntimeException("Certificate file: $filename is not found.")
        }

        try
        {
            return BufferedInputStream(FileInputStream(file)).use { stream ->
                val certFactory = CertificateFactory.getInstance("X.509")
                certFactory.generateCertificates(stream) as List<Certificate?>
            }
        }
        catch (ex: Exception)
        {
            throw RuntimeException("Failed to load certificate file $filename.", ex)
        }
    }

    private fun loadPrivateKeyFromFile(privateKeyPathname: String, algorithm: String? = null): PrivateKey
    {
        val privateKey: PrivateKey
        val privateKeyFile = File(privateKeyPathname)

        if(!privateKeyFile.exists())
        {
            throw RuntimeException("Private Key file not found: ${privateKeyFile.absolutePath}")
        }

        try
        {
            privateKey = DataInputStream(FileInputStream(privateKeyFile)).use { stream ->
                PrivateKeyReader.getPrivateKey(stream, algorithm)
            }
        }
        catch(ex: Exception)
        {
            throw RuntimeException("Failed to load Private Key from file ${privateKeyFile.absolutePath}.", ex)
        }

        return privateKey
    }
}
