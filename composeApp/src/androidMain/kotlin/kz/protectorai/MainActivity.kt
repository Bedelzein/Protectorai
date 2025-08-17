package kz.protectorai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.Cancellation
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import kz.protectorai.navigation.RootComponent
import kz.protectorai.navigation.RootContent
import com.arkivanov.decompose.Child

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val root = RootComponent.Default(defaultComponentContext())

        setContent { RootContent(root) }
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