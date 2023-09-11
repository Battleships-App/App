package com.isel.battleshipsAndroid.leaderboard.ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.isel.battleshipsAndroid.R
import com.isel.battleshipsAndroid.leaderboard.model.PlayerInfo
import com.isel.battleshipsAndroid.ui.TopBar
import com.isel.battleshipsAndroid.ui.theme.BattleshipsAndroidTheme

const val PlayerFoundViewTag = "PlayerFoundView"
const val RankingsViewTag = "RankingsView"
const val LeaderboardScreenTag = "LeaderboardScreenTag"


data class LeaderboardState(
    val playerFound: PlayerInfo? = null,
    val rankings: List<PlayerInfo>? = null,
    val error: String? = null,
)

@Composable
fun PlayerView(
    playerInfo: PlayerInfo,
){
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            Text(
                text = playerInfo.username,
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
            )
            Text(
                text = "${playerInfo.games}",
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
            )
            Text(
                text = "${playerInfo.points}",
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
            )
        }
    }
}


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LeaderboardView(
    state: LeaderboardState = LeaderboardState(),
    onFindPlayer: (String) -> Unit,
    onBackRequest: () -> Unit,
    onErrorReset: () -> Unit,
) {

    val username = rememberSaveable { mutableStateOf("") }
    val currentUsername = username.value

    BattleshipsAndroidTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag(RankingsViewTag),
            backgroundColor = MaterialTheme.colors.background,
            topBar = { TopBar(onBackRequested = { onBackRequest() }) },
        ) { innerPadding ->
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.leaderboard_searchforplayer),
                    style = MaterialTheme.typography.h5,
                    color = MaterialTheme.colors.primaryVariant
                )
                Row() {
                    Text(
                        text = stringResource(id = R.string.leaderboard_name),
                        style = MaterialTheme.typography.subtitle2,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        modifier = Modifier
                            .padding(start = 8.dp, top = 24.dp, end = 8.dp, bottom = 8.dp),
                    )
                    TextField(
                        value = currentUsername,
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color(0xFFFFFFFF),
                            textColor = Color(0xFF000000)
                        ),
                        onValueChange = { username.value = ensureInputBounds(it) }
                    )
                    IconButton(onClick = {
                        onFindPlayer(currentUsername)
                    }) {
                        Icon(Icons.Default.PersonSearch, contentDescription = null)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (state.playerFound != null) {
                    Row {
                        PlayerView(playerInfo = state.playerFound)
                    }
                }

                if (state.rankings != null) {
                    Text(
                        text = stringResource(id = R.string.leaderboard_leaderboard),
                        style = MaterialTheme.typography.h4,
                        color = MaterialTheme.colors.primaryVariant
                    )
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(innerPadding),
                        userScrollEnabled = true,
                    ) {
                        items(state.rankings.size) {
                            PlayerView(state.rankings[it])
                        }
                    }
                }else{
                    Text(
                        text = stringResource(id = R.string.leaderboard_leaderboard),
                        style = MaterialTheme.typography.h4,
                        color = MaterialTheme.colors.primaryVariant
                    )
                    Text(
                        text = stringResource(id = R.string.utils_loading),
                        style = MaterialTheme.typography.h5,
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

private const val MAX_INPUT_SIZE = 32
private fun ensureInputBounds(input: String) =
    input.also {
        it.substring(range = 0 until Integer.min(it.length, MAX_INPUT_SIZE))
    }

@Preview(showBackground = true)
@Composable
private fun LeaderboardWithoutPlayerPreview() {
    LeaderboardView(
        state = LeaderboardState(null, leaderboard),
        onFindPlayer = {},
        onBackRequest = {},
        onErrorReset = {},
    )
}


@Preview(showBackground = true)
@Composable
private fun LeaderboardPreview() {
    val sut = PlayerInfo("Teste", 1, 1)
    LeaderboardView(
        state = LeaderboardState(sut, leaderboard),
        onFindPlayer = {},
        onBackRequest = {},
        onErrorReset = {},
    )
}

private val leaderboard = buildList {
    repeat(30) {
        add(PlayerInfo("Player$it", it, it))
    }
}.asReversed()