package com.isel.battleshipsAndroid.login.ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.isel.battleshipsAndroid.R
import com.isel.battleshipsAndroid.login.model.Token
import com.isel.battleshipsAndroid.ui.RefreshingState
import com.isel.battleshipsAndroid.ui.TopBar
import com.isel.battleshipsAndroid.ui.theme.BattleshipsAndroidTheme
import java.lang.Integer.min

const val LoginScreenTag = "LoginScreen"
const val NameInputTag = "NameInput"
const val PasswordInputTag = "PasswordInput"

data class LoginScreenState(
    val token: Token? = null,
    val loadingState: Boolean = false,
    val error: String? = null,
)

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LoginView(
    state: LoginScreenState = LoginScreenState(),
    onSignupRequest: ((username: String, password: String) -> Unit)? = null,
    onSignInRequest: ((username: String, password: String) -> Unit)? = null,
    onBackRequest: () -> Unit,
) {
    val username = rememberSaveable { mutableStateOf("") }
    val currentUsername = username.value

    val password = rememberSaveable { mutableStateOf("") }
    val currentPassword = password.value

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    BattleshipsAndroidTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize().testTag(LoginScreenTag),
            backgroundColor = MaterialTheme.colors.background,
            topBar = {
                TopBar(onBackRequested = { onBackRequest() })
            }
        ) { innerPadding ->
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                if (!state.loadingState) {
                    Row() {
                        Text(
                            text = stringResource(id = R.string.login_please_introduce_your_credentials),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Text(text = stringResource(id = R.string.login_username), modifier = Modifier.padding(8.dp))
                    Row() {
                        TextField(
                            value = currentUsername,
                            singleLine = true,
                            modifier = Modifier.testTag(NameInputTag),
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color(0xFFFFFFFF),
                                textColor = Color(0xFF000000)
                            ),
                            onValueChange = { username.value = ensureInputBounds(it) }
                        )
                    }
                    Text(text =  stringResource(id = R.string.login_password), modifier = Modifier.padding(8.dp))
                    Row() {
                        TextField(
                            value = currentPassword,
                            singleLine = true,
                            modifier = Modifier.testTag(PasswordInputTag),
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color(0xFFFFFFFF),
                                textColor = Color(0xFF000000)
                            ),
                            onValueChange = { password.value = ensureInputBounds(it) },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                val image = if (passwordVisible)
                                    Icons.Filled.Visibility
                                else Icons.Filled.VisibilityOff

                                // Please provide localized description for accessibility services
                                val description =
                                    if (passwordVisible) stringResource(id = R.string.login_hide_pass) else stringResource(id = R.string.login_show_password)

                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = image, description)
                                }
                            }
                        )
                    }
                    Row() {
                        Button(onClick = {
                            if (onSignupRequest != null) {
                                onSignupRequest(currentUsername, currentPassword)
                            }
                        }) {
                            Text(text = stringResource(id = R.string.login_register_button))
                        }
                        Spacer(modifier = Modifier.width(32.dp))
                        Button(onClick = {
                            if (onSignInRequest != null) {
                                onSignInRequest(currentUsername, currentPassword)
                            }
                        }) {
                            Text(text = stringResource(id = R.string.login_signin_button))
                        }
                    }
                    if (state.error != null) {
                        Toast.makeText(LocalContext.current, state.error, Toast.LENGTH_LONG)
                            .show()
                    }

                } else {
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        Text(
                            text = stringResource(id = R.string.utils_waiting),
                            style = MaterialTheme.typography.h2,
                            color = MaterialTheme.colors.primaryVariant
                        )
                    }
                }
            }
        }
    }
}


private const val MAX_INPUT_SIZE = 32
private fun ensureInputBounds(input: String) =
    input.also {
        it.substring(range = 0 until min(it.length, MAX_INPUT_SIZE))
    }


@Preview(showBackground = true)
@Composable
private fun SignInOrSignUpWaitingForInputPreview() {
    fun signUp(name: String, pass: String): Unit {}
    fun signIn(name: String, pass: String): Unit {}
    LoginView(
        state = LoginScreenState(null, false, null),
        onSignupRequest = ::signUp,
        onSignInRequest = ::signIn,
        onBackRequest = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun SignInOrSignUpWaitingForResponsePreview() {
    fun signUp(name: String, pass: String): Unit {}
    fun signIn(name: String, pass: String): Unit {}
    LoginView(
        state = LoginScreenState(null, true, null),
        onSignupRequest = ::signUp,
        onSignInRequest = ::signIn,
        onBackRequest = {}
    )
}