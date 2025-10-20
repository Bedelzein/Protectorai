package kz.protectorai.navigation.auth

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kz.protectorai.CommonHardcode
import kz.protectorai.core.EMPTY_STRING
import kz.protectorai.core.Eventful
import kz.protectorai.core.Stateful
import kz.protectorai.core.coroutineScope
import kz.protectorai.data.ClientRepository
import kz.protectorai.data.GuestRepository
import kz.protectorai.navigation.RootComponent
import kz.protectorai.util.FirebaseUtil
import kotlin.jvm.JvmInline

class AuthComponent(
    componentContext: ComponentContext,
    private val onEvent: Eventful<RootComponent.Event>
) : ComponentContext by componentContext,
    Stateful<AuthComponent.State> by Stateful.Default(State()),
    Eventful<AuthComponent.Event> {

    private val scope by lazy { coroutineScope(SupervisorJob()) }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.UsernameChanged -> updateState { copy(username = event.value) }
            is Event.PasswordChanged -> updateState { copy(password = event.value) }
            is Event.AuthButtonClicked -> {
                updateState { copy(isAuthInProgress = true) }
                scope.launch {
                    val (accessToken, _) = GuestRepository.auth(
                        state.username,
                        state.password
                    )
                    ClientRepository.getInstance(accessToken)
                    try {
                        FirebaseUtil.default.registerFirebaseToken()
                    } catch (e: Exception) {

                    }
                    withContext(Dispatchers.Main) {
                        onEvent(RootComponent.Event.AuthCompleted(accessToken))
                    }
                }
            }
        }
    }

    data class State(
        val username: String = CommonHardcode.wildcard { "sko_bilim" },
        val password: String = CommonHardcode.wildcard { "sko_bilim!" },
        val isAuthInProgress: Boolean = false
    ) {
        val isReadyToAuth = username.isNotBlank() && password.isNotBlank() && !isAuthInProgress
    }

    sealed interface Event : Eventful.Event {
        @JvmInline
        value class UsernameChanged(val value: String) : Event
        @JvmInline
        value class PasswordChanged(val value: String) : Event
        data object AuthButtonClicked : Event
    }
}