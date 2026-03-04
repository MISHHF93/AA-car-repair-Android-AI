package com.aa.carrepair.core.security

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val keysetName = "aa_carrepair_keyset"
    private val prefFileName = "aa_carrepair_security_prefs"
    private val masterKeyUri = "android-keystore://aa_carrepair_master_key"

    private val aead: Aead by lazy {
        AeadConfig.register()
        AndroidKeysetManager.Builder()
            .withSharedPref(context, keysetName, prefFileName)
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri(masterKeyUri)
            .build()
            .keysetHandle
            .getPrimitive(Aead::class.java)
    }

    fun encrypt(plaintext: String, associatedData: String = ""): ByteArray? {
        return try {
            aead.encrypt(
                plaintext.toByteArray(StandardCharsets.UTF_8),
                associatedData.toByteArray(StandardCharsets.UTF_8)
            )
        } catch (e: Exception) {
            Timber.e(e, "Encryption failed")
            null
        }
    }

    fun decrypt(ciphertext: ByteArray, associatedData: String = ""): String? {
        return try {
            aead.decrypt(
                ciphertext,
                associatedData.toByteArray(StandardCharsets.UTF_8)
            ).toString(StandardCharsets.UTF_8)
        } catch (e: Exception) {
            Timber.e(e, "Decryption failed")
            null
        }
    }

    fun encryptVin(vin: String): ByteArray? = encrypt(vin, "vin_context")

    fun decryptVin(ciphertext: ByteArray): String? = decrypt(ciphertext, "vin_context")
}
