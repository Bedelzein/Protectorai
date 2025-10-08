package kz.protectorai

import android.app.Application
import kz.protectorai.util.FirebaseUtil

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseUtil.default = AndroidFirebaseUtil(applicationContext)
    }
}