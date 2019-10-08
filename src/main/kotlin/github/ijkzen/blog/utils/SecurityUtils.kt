package github.ijkzen.blog.utils

import org.apache.commons.codec.binary.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

class SecurityUtils {

    companion object {
        private const val key = "Talk is cheap, show me the code"

        fun encryption(plainText: String): String {
            val cipher = Cipher.getInstance(DES)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
            return Base64().encodeAsString(cipher.doFinal(plainText.toByteArray()))
        }

        fun decryption(cipherText: String): String {
            val cipher = Cipher.getInstance(DES)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey())
            val result = Base64().decode(cipherText)
            return String(cipher.doFinal(result))
        }

        private fun getSecretKey() = SecretKeyFactory.getInstance(DES)
                .generateSecret(DESKeySpec(key.toByteArray()))

    }
}