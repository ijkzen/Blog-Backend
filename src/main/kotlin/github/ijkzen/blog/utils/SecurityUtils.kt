package github.ijkzen.blog.utils

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

class SecurityUtils {

    init {
        System.err.println("input length: ${key.length}")
    }

    companion object {
        var key = "Talk is cheap, show me the code"

        private val random = SecureRandom()

        fun encryption(plainText: String): String {
            val cipher = Cipher.getInstance(DES)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), random)

            return String(cipher.doFinal(plainText.toByteArray()))
        }

        fun decryption(cipherText: String): String {
            val cipher = Cipher.getInstance(DES)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), random)

            return String(cipher.doFinal(cipherText.toByteArray()))
        }

        private fun getSecretKey() = SecretKeyFactory.getInstance(DES)
                .generateSecret(DESKeySpec(key.toByteArray()))

    }
}