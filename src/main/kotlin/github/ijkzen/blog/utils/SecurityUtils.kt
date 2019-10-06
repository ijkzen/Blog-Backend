package github.ijkzen.blog.utils

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

class SecurityUtils {
    companion object {
        var key = "Talk is cheap, show me the code"

        private val random = SecureRandom()

        @ExperimentalStdlibApi
        fun encryption(plainText: String): String {
            val cipher = Cipher.getInstance(DES)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), random)

            return cipher.doFinal(plainText.toByteArray()).decodeToString()
        }

        @ExperimentalStdlibApi
        fun decryption(cipherText: String): String {
            val cipher = Cipher.getInstance(DES)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), random)

            return cipher.doFinal(cipherText.toByteArray()).decodeToString()
        }

        private fun getSecretKey() = SecretKeyFactory.getInstance(DES)
                .generateSecret(DESKeySpec(key.toByteArray()))

    }
}