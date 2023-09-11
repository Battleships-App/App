package com.isel.battleshipsAndroid.me.ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.isel.battleshipsAndroid.R
import com.isel.battleshipsAndroid.game.model.GameDTO
import com.isel.battleshipsAndroid.leaderboard.model.PlayerInfo
import com.isel.battleshipsAndroid.ui.TopBar
import com.isel.battleshipsAndroid.ui.theme.BattleshipsAndroidTheme


const val MePageScreenTagIdle = "MePageScreenIdleTag"
const val MePageScreenTagLoading = "MePageScreenLoadingTag"
const val MyInfoViewTag = "PlayerInfoView"
const val GameEndedViewTag = "GameEndedViewTag"

data class MeScreenState(
    val myInfo: PlayerInfo? = null,
    val myGamesHistory: List<GameDTO>? = null,
    val error: String? = null,
)

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MeView(
    state: MeScreenState = MeScreenState(),
    onBackRequest: () -> Unit,
    onErrorReset: () -> Unit,
) {
    BattleshipsAndroidTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag(MePageScreenTagIdle),
            backgroundColor = MaterialTheme.colors.background,
            topBar = {
                TopBar(
                    onBackRequested = { onBackRequest() })
            }
        ) { innerPadding ->
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                if (state.myInfo == null && state.myGamesHistory == null) {
                    Text(
                        modifier = Modifier.testTag(MePageScreenTagLoading),
                        text = stringResource(id = R.string.utils_waiting),
                        style = MaterialTheme.typography.h3,
                        color = MaterialTheme.colors.primaryVariant
                    )
                } else {
                    if (state.myInfo != null) {
                        Text(
                            text = stringResource(id = R.string.me_yourinfo),
                            style = MaterialTheme.typography.h3,
                            color = MaterialTheme.colors.primaryVariant
                        )
                        Row(
                            modifier = Modifier.padding(innerPadding),
                        ) {
                            ProfileView(state.myInfo)
                        }
                    } else {
                        Text(
                            text = stringResource(id = R.string.me_yourinfo),
                            style = MaterialTheme.typography.h3,
                            color = MaterialTheme.colors.primaryVariant
                        )
                        Text(
                            text = stringResource(id = R.string.utils_loading),
                            style = MaterialTheme.typography.h4,
                            color = MaterialTheme.colors.primaryVariant
                        )
                    }

                    if (state.myGamesHistory != null) {
                        Text(
                            text = stringResource(id = R.string.me_yourgamehistory),
                            style = MaterialTheme.typography.h4,
                            color = MaterialTheme.colors.primaryVariant
                        )
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(innerPadding),
                            userScrollEnabled = true,
                        ) {
                            items(state.myGamesHistory.size) {
                                GamesHistoryView(state.myGamesHistory[it])
                            }
                        }
                    } else {
                        Text(
                            text = stringResource(id = R.string.me_yourgamehistory),
                            style = MaterialTheme.typography.h3,
                            color = MaterialTheme.colors.primaryVariant
                        )
                        Text(
                            text = stringResource(id = R.string.utils_loading),
                            style = MaterialTheme.typography.h4,
                            color = MaterialTheme.colors.primaryVariant
                        )
                    }
                    if (state.error != null) {
                        Toast.makeText(LocalContext.current, state.error, Toast.LENGTH_LONG).show()
                        onErrorReset()
                    }
                }
            }
        }
    }
}


@Composable
fun ProfileView(player: PlayerInfo) {
    Card(
        shape = MaterialTheme.shapes.small,
        elevation = 2.dp,
        modifier = Modifier
            .testTag(MyInfoViewTag)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text =  stringResource(id = R.string.me_name)+" ${player.username}",
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
            )
            Text(
                text = stringResource(id = R.string.me_gamesplayed)+ " ${player.games}",
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
            )
            Text(
                text = stringResource(id = R.string.me_totalpoints)+ "${player.points}",
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
            )
        }
    }
}

@Composable
fun GamesHistoryView(gameHistory: GameDTO) {
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(GameEndedViewTag)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            Text(
                text = gameHistory.player1,
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
            )
            Text(
                text = gameHistory.player2,
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
            )
            Text(
                text = "${gameHistory.result}",
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun MePagePreview() {
    MeView(
        state = MeScreenState(PlayerInfo("Guest", 4, 3), myGamesHistory),
        onBackRequest = {},
        onErrorReset = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun MePageLoadingPreview() {
    MeView(
        state = MeScreenState(null, null),
        onBackRequest = {},
        onErrorReset = {},
    )
}

private val myGamesHistory = buildList {
    repeat(10) {
        add(GameDTO("$it", "ENDED", "test", "test", "test", "___", "___", "a1", 1, 1))
    }
}
