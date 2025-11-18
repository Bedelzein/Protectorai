package kz.protectorai

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kz.protectorai.data.ClientRepository
import kz.protectorai.util.FirebaseUtil

private var registrationTrigger: FirebaseRegistrationTriggerContract? = null

fun setRegistrationTrigger(trigger: FirebaseRegistrationTriggerContract?) {
    registrationTrigger = trigger
}

fun registerFirebaseToken(deviceId: String, firebaseToken: String) {
    CoroutineScope(SupervisorJob()).launch(Dispatchers.IO) {
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