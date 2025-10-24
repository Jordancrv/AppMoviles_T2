package com.example.juego.Sesion

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.security.MessageDigest
import java.util.Hashtable

object QRHelper {

    /**
     * Genera un token seguro para QR basado en ID y timestamp
     */
    fun generateSecureToken(playerId: Int, username: String): String {
        val timestamp = System.currentTimeMillis()
        val data = "$playerId:$username:$timestamp"
        return hashSHA256(data)
    }

    /**
     * Genera un Bitmap del código QR
     * @param content: Contenido del QR (el token seguro)
     * @param size: Tamaño del QR en píxeles
     */
    fun generateQRCode(content: String, size: Int = 512): Bitmap? {
        return try {
            val hints = Hashtable<EncodeHintType, Any>()
            hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
            hints[EncodeHintType.MARGIN] = 1

            val qrCodeWriter = QRCodeWriter()
            val bitMatrix = qrCodeWriter.encode(
                content,
                BarcodeFormat.QR_CODE,
                size,
                size,
                hints
            )

            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(
                        x,
                        y,
                        if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                    )
                }
            }
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Hash SHA-256 para crear token seguro
     */
    private fun hashSHA256(data: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(data.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Valida el formato del token QR
     */
    fun isValidQRToken(token: String): Boolean {
        // Token SHA-256 tiene 64 caracteres hexadecimales
        return token.matches(Regex("^[a-f0-9]{64}$"))
    }
}
