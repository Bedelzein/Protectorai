package kz.protectorai

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kz.protectorai.data.ClientRepository
import kz.protectorai.util.FirebaseUtil

private const val WORKER_KEY_IS_FIREBASE_TOKEN_REGISTER_PROCESS = "isRegister"
private const val WORKER_KEY_FIREBASE_TOKEN = "WORKER_KEY_FIREBASE_TOKEN"

class AndroidFirebaseUtil(private val appContext: Context) : FirebaseUtil {

    override fun registerFirebaseToken(token: String?) {
        val data = Data.Builder()
            .putBoolean(WORKER_KEY_IS_FIREBASE_TOKEN_REGISTER_PROCESS, true)
            .also { if (token != null) it.putString(WORKER_KEY_FIREBASE_TOKEN, token) }
            .build()
        val workRequest = OneTimeWorkRequestBuilder<RegisterTokenWorker>().setInputData(data).build()
        WorkManager.getInstance(appContext).enqueue(workRequest)
    }

    override fun unregisterFirebaseToken() {
        val data = Data.Builder()
            .putBoolean(WORKER_KEY_IS_FIREBASE_TOKEN_REGISTER_PROCESS, false)
            .build()
        val workRequest = OneTimeWorkRequestBuilder<RegisterTokenWorker>().setInputData(data).build()
        WorkManager.getInstance(appContext).enqueue(workRequest)
    }

    class RegisterTokenWorker(
        context: Context,
        workerParams: WorkerParameters
    ) : CoroutineWorker(context, workerParams) {
        override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
            try {
                val id = FirebaseInstallations.getInstance().id.await()
                val isRegister = inputData.getBoolean(
                    WORKER_KEY_IS_FIREBASE_TOKEN_REGISTER_PROCESS,
                    false
                )
                if (isRegister) {
                    val token = inputData.getString(WORKER_KEY_FIREBASE_TOKEN)
                        ?: FirebaseMessaging.getInstance().token.await()
                    ClientRepository.getInstanceUnsafe().registerFirebaseToken(id, token)
                } else {
                    ClientRepository.getInstanceUnsafe().unregisterFirebaseToken(id)
                }
                return@withContext Result.success()
            } catch (e: Exception) {
                return@withContext Result.failure(
                    Data.Builder()
                        .putString("error", e.localizedMessage)
                        .build()
                )
            }
        }
    }
}