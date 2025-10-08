package kz.protectorai.navigation.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kz.protectorai.core.Eventful

@Composable
fun AuthContent(state: AuthComponent.State, onEvent: Eventful<AuthComponent.Event>) {
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .align(Alignment.Center)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.username,
                onValueChange = { onEvent(AuthComponent.Event.UsernameChanged(it)) },
                label = { Text("Username") }
            )
            Spacer(Modifier.size(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.password,
                onValueChange = { onEvent(AuthComponent.Event.PasswordChanged(it)) },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()
            )
        }
        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            onClick = { onEvent(AuthComponent.Event.AuthButtonClicked) },
            enabled = !state.isAuthInProgress && state.isReadyToAuth
        ) {
            if (state.isAuthInProgress) CircularProgressIndicator() else Text("Auth")
        }
    }
}