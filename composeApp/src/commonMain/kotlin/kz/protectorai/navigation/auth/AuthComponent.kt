package kz.protectorai.navigation.auth

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kz.protectorai.core.Eventful
import kz.protectorai.core.Stateful
import kz.protectorai.core.coroutineScope
import kz.protectorai.data.ProtectoraiRepository
import kotlin.jvm.JvmInline

class AuthComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext,
    Stateful<AuthComponent.State> by Stateful.Default(State()),
    Eventful<AuthComponent.Event> {

    private val scope by lazy { coroutineScope(SupervisorJob()) }

    private val repository by lazy { ProtectoraiRepository() }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.UsernameChanged -> updateState { copy(username = event.value) }
            is Event.PasswordChanged -> updateState { copy(password = event.value) }
            is Event.AuthButtonClicked -> {
                updateState { copy(isAuthInProgress = true) }
                scope.launch {
                    val (access, refresh) = repository.auth(
                        state.username,
                        state.password
                    )
                    // TODO
                }
            }
        }
    }

    data class State(
        val username: String = "super_user",
        val password: String = "super_password",
        val isAuthInProgress: Boolean = false
    ) {
        fun isReadyToAuth() = username.isNotBlank() && password.isNotBlank() && !isAuthInProgress
    }

    sealed interface Event : Eventful.Event {
        @JvmInline
        value class UsernameChanged(val value: String) : Event
        @JvmInline
        value class PasswordChanged(val value: String) : Event
        data object AuthButtonClicked : Event
    }
}