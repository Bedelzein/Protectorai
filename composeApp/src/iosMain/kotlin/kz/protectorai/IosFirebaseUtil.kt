package kz.protectorai

import kotlinx.coroutines.runBlocking
import kz.protectorai.data.ClientRepository
import kz.protectorai.util.FirebaseUtil

private var registrationTrigger: FirebaseRegistrationTriggerContract? = null

fun setRegistrationTrigger(trigger: FirebaseRegistrationTriggerContract?) {
    registrationTrigger = trigger
}

fun registerFirebaseToken(deviceId: String, firebaseToken: String) {
    runBlocking {
        try {
            ClientRepository.getInstanceUnsafe().registerFirebaseToken(deviceId, firebaseToken)
        } catch (e: Throwable) {
            println(e.message ?: e.toString())
        }
    }
}

class IosFirebaseUtil : FirebaseUtil {

    override fun registerFirebaseToken(token: String?) {
        registrationTrigger?.trigger()
    }

    override fun unregisterFirebaseToken() {
        // TODO
    }
}

interface FirebaseRegistrationTriggerContract {
    fun trigger()
}