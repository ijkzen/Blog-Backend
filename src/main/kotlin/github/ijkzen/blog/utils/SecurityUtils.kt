package github.ijkzen.blog.utils

import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

class SecurityUtils {

    companion object {
        private const val key = "Talk is cheap, show me the code"

        fun encryption(plainText: String): String {
            val cipher = Cipher.getInstance(DES)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
            return String(cipher.doFinal(plainText.toByteArray()))
        }

        fun decryption(cipherText: String): String {
            val cipher = Cipher.getInstance(DES)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey())
            System.err.println("input length: ${cipherText.toByteArray().size}")
            return String(cipher.doFinal(cipherText.toByteArray()))
        }

        private fun getSecretKey() = SecretKeyFactory.getInstance(DES)
                .generateSecret(DESKeySpec(key.toByteArray()))

    }
}