package com.isel.battleshipsAndroid.lobby.ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.isel.battleshipsAndroid.game.model.Room
import com.isel.battleshipsAndroid.ui.TopBar
import com.isel.battleshipsAndroid.ui.theme.BattleshipsAndroidTheme

const val GameInfoViewTag = "GameInfoView"
const val LobbyScreenTag = "LobbyScreenTag"

data class LobbyState(
    val games: List<Room> = emptyList(),
    val error: String? = null,
)

@Composable
fun GameInfoView(
    gameInfo: Room,
    onGameSelected: () -> Unit,
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onGameSelected() }
            .testTag(GameInfoViewTag)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = gameInfo.player1,
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(id = R.string.lobby_shotsperround) + gameInfo.spr.toString(),
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, end = 8.dp),
            )
        }
    }
}

@Composable
fun CreateGameView(
    onCreateSelected: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text =  stringResource(id = R.string.lobby_spr)+": 1",
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier
                .padding(start = 8.dp, top = 13.dp, end = 8.dp, bottom = 16.dp),
        )
        IconButton(onClick = {
            onCreateSelected()
        }) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
    }
}


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LobbyView(
    state: LobbyState = LobbyState(),
    onJoinGame: (String) -> Unit,
    onCreateGame: (Int) -> Unit,
    onBackRequest: () -> Unit,
    onRefreshRequest: () -> Unit,
    onErrorReset: () -> Unit,
) {

    BattleshipsAndroidTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag(LobbyScreenTag),
            backgroundColor = MaterialTheme.colors.background,
            topBar = {
                TopBar(
                    onBackRequested = { onBackRequest() },
                    onRefreshRequested = { onRefreshRequest() })
            },
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
                    text = stringResource(id = R.string.lobby_lobby),
                    style = MaterialTheme.typography.h5,
                    color = MaterialTheme.colors.primaryVariant
                )
                CreateGameView(onCreateSelected = { onCreateGame(1) })
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(innerPadding)
                ) {
                    items(state.games.size) {
                        GameInfoView(
                            gameInfo = state.games[it],
                            onGameSelected = { onJoinGame(state.games[it].uuid) }
                        )
                    }
                }
                if (state.error != null) {
                    Toast.makeText(LocalContext.current, state.error, Toast.LENGTH_SHORT).show()
                    onErrorReset()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun GameInfoPreview() {
    GameInfoView(
        gameInfo = Room("123", "Guest", 1),
        onGameSelected = { }
    )
}

@Preview(showBackground = true)
@Composable
private fun CreateGamePreview() {
    CreateGameView(onCreateSelected = { })
}

@Preview(showBackground = true)
@Composable
private fun LobbyPreview() {
    LobbyView(
        state = LobbyState(games),
        onJoinGame = {},
        onCreateGame = {},
        onBackRequest = {},
        onRefreshRequest = {},
        onErrorReset = {},
    )
}

private val games = buildList {
    repeat(30) {
        add(Room("$it", "Player$it", 1))
    }
}
