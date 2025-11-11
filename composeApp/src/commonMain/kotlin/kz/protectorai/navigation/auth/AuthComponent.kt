package kz.protectorai.navigation.auth

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kz.protectorai.core.EMPTY_STRING
import kz.protectorai.core.Eventful
import kz.protectorai.core.Payload
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
            is Event.UsernameChanged -> updateState {
                copy(
                    username = event.value,
                    errorText = null
                )
            }

            is Event.PasswordChanged -> updateState {
                copy(
                    password = event.value,
                    errorText = null
                )
            }

            is Event.AuthButtonClicked -> auth()
        }
    }

    private fun auth() {
        updateState { copy(isAuthInProgress = true) }
        scope.launch {
            when (val payload = GuestRepository.auth(state.username, state.password)) {
                is Payload.Success -> {
                    val accessToken = payload.data.accessToken
                    ClientRepository.getInstance(accessToken)
                    runCatching { FirebaseUtil.default.registerFirebaseToken() }
                    withContext(Dispatchers.Main) {
                        onEvent(RootComponent.Event.AuthCompleted(accessToken))
                    }
                }

                is Payload.Failure -> updateState {
                    copy(
                        password = EMPTY_STRING,
                        isAuthInProgress = false,
                        errorText = payload.message
                    )
                }
            }
        }
    }

    data class State(
        val username: String = "test_user",// EMPTY_STRING,
        val password: String = "grlcknlc",// EMPTY_STRING,
        val isAuthInProgress: Boolean = false,
        val errorText: String? = null
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