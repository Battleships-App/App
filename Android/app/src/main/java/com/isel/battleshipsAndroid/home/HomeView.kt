package com.isel.battleshipsAndroid.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.isel.battleshipsAndroid.R
import com.isel.battleshipsAndroid.login.model.Token
import com.isel.battleshipsAndroid.login.model.UserInfo
import com.isel.battleshipsAndroid.ui.LoggedState
import com.isel.battleshipsAndroid.ui.RefreshingState
import com.isel.battleshipsAndroid.ui.TopBar
import com.isel.battleshipsAndroid.ui.theme.BattleshipsAndroidTheme

data class HomeScreenState(
    val user: UserInfo? = null,
    val loggedState: Boolean = false
)

const val HomeScreenTag = "HomeScreen"

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeView(
    state: HomeScreenState = HomeScreenState(),
    onLogoutRequest: () -> Unit,
    onMeRequest: () -> Unit,
    onFindGameRequest: () -> Unit,
    onLeaderboardRequest: () -> Unit,
    onInfoRequest: () -> Unit,
    onSignInOrSignUpRequest: () -> Unit,
    onExitRequest: () -> Unit
) {
    BattleshipsAndroidTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize().testTag(HomeScreenTag),
            backgroundColor = MaterialTheme.colors.background,
            topBar = {
                if (state.loggedState) {
                    TopBar(
                        onInfoRequested = { onInfoRequest() },
                        onLogoutRequested = { onLogoutRequest() })
                } else {
                    TopBar(
                        onInfoRequested = { onInfoRequest() },
                    )
                }
            }
        ) { innerPadding ->
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Row() {
                    Text(
                        text = "BATTLESHIPS",
                        style = MaterialTheme.typography.h2,
                        color = MaterialTheme.colors.primaryVariant
                    )
                }
                ButtonView(
                    onClickRequest = { onFindGameRequest() },
                    name = stringResource(id = R.string.home_play_button),
                    state = state.loggedState,
                    testTag = "PlayButton"
                )
                ButtonView(
                    onClickRequest = { onMeRequest() },
                    name = stringResource(id = R.string.home_profile_button),
                    state = state.loggedState,
                    testTag = "ProfileButton"

                )
                ButtonView(onClickRequest = { onLeaderboardRequest() },
                    name = stringResource(id = R.string.home_leaderboard_button),
                    testTag = "LeaderboardButton"
                )
                if (!state.loggedState) {
                    ButtonView(
                        onClickRequest = { onSignInOrSignUpRequest() },
                        name = stringResource(id = R.string.home_signinup_button),
                        testTag = "SignInAndSignUpButton"
                    )
                }
                ButtonView(
                    onClickRequest = { onExitRequest() },
                    name = stringResource(id = R.string.home_exit_button),
                    testTag = "ExitButton"
                )
            }
        }
    }
}

@Composable
fun ButtonView(onClickRequest: () -> Unit, name: String, state: Boolean? = null, testTag: String) {
    if (state == null) {
        Row() {
            Button(onClick = { onClickRequest() }, modifier = Modifier.testTag(testTag)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.h5,
                )
            }
        }
    } else {
        Row() {
            Button(onClick = { onClickRequest() }, enabled = state, modifier = Modifier.testTag(testTag)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.h5,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomePreviewLoggedIn() {
    HomeView(
        state = HomeScreenState(UserInfo("Teste", "Teste"), true),
        onLogoutRequest = {},
        onMeRequest = {},
        onFindGameRequest = {},
        onLeaderboardRequest = {},
        onInfoRequest = {},
        onSignInOrSignUpRequest = {},
        onExitRequest = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun HomePreviewLoggedOut() {
    HomeView(
        state = HomeScreenState(null, false),
        onLogoutRequest = {},
        onMeRequest = {},
        onFindGameRequest = {},
        onLeaderboardRequest = {},
        onInfoRequest = {},
        onSignInOrSignUpRequest = {},
        onExitRequest = {}
    )
}