package kz.protectorai

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.arkivanov.decompose.Cancellation
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import kz.protectorai.navigation.RootComponent
import kz.protectorai.navigation.RootContent
import com.arkivanov.decompose.Child
import kz.protectorai.data.initPreferencesDataStore
import kz.protectorai.util.FirebaseUtil

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            FirebaseUtil.default = AndroidFirebaseUtil(applicationContext)
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        initPreferencesDataStore(applicationContext)
        val root = RootComponent.Default(defaultComponentContext())
        setContent { RootContent(root) }
        askNotificationPermission()
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )

            if (notificationPermission == PackageManager.PERMISSION_GRANTED) {
                FirebaseUtil.default = AndroidFirebaseUtil(applicationContext)
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    val root = object : RootComponent {
        override val stack = object : Value<ChildStack<*, RootComponent.Child>>() {
            override val value = ChildStack(
                Child.Created(RootComponent.Config, RootComponent.Child.Auth(TODO()))
            )

            override fun subscribe(
                observer: (ChildStack<*, RootComponent.Child>) -> Unit
            ) = Cancellation {}
        }
        override fun onBackPressed() = Unit
        override fun onBackClicked(toIndex: Int) = Unit
        override fun onEvent(event: RootComponent.Event) = Unit
    }
    RootContent(root)
}