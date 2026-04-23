package com.example.taldea5

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoHelper {

    private val SHARED_KEY = "euskal jatetxe gako sekretua bat".toByteArray(Charsets.UTF_8)

    init {
        require(SHARED_KEY.size == 32) { "Gakoak 32 bytetakoa izan behar du. Egungoa: ${SHARED_KEY.size}" }
    }

    fun cifrar(plainText: String): String {
        if (plainText.isBlank()) return ""
        
        return try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val secretKey = SecretKeySpec(SHARED_KEY, 0, SHARED_KEY.size, "AES")
            
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            
            val plainBytes = plainText.toByteArray(Charsets.UTF_8)
            val cipherBytes = cipher.doFinal(plainBytes)
            
            val iv = cipher.iv
            val result = ByteArray(iv.size + cipherBytes.size)
            System.arraycopy(iv, 0, result, 0, iv.size)
            System.arraycopy(cipherBytes, 0, result, iv.size, cipherBytes.size)
            
            Base64.encodeToString(result, Base64.NO_WRAP)
        } catch (e: Exception) {
            throw Exception("Errorea zifratzean: ${e.message}", e)
        }
    }

    fun descifrar(cipherText: String): String {
        if (cipherText.isBlank()) return ""
        
        return try {
            val buffer = Base64.decode(cipherText, Base64.NO_WRAP)
            
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val secretKey = SecretKeySpec(SHARED_KEY, 0, SHARED_KEY.size, "AES")
            
            val iv = ByteArray(16)
            System.arraycopy(buffer, 0, iv, 0, iv.size)
            
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
            
            val cipherBytes = ByteArray(buffer.size - iv.size)
            System.arraycopy(buffer, iv.size, cipherBytes, 0, cipherBytes.size)
            
            val plainBytes = cipher.doFinal(cipherBytes)
            
            String(plainBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            throw Exception("Errorea deszifratzean: ${e.message}", e)
        }
    }
}
