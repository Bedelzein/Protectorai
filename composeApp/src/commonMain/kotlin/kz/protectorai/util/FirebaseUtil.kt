package kz.protectorai.util

interface FirebaseUtil {

    fun registerFirebaseToken(token: String? = null)

    fun unregisterFirebaseToken()

    companion object {
        lateinit var default: FirebaseUtil
    }
}