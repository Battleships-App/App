package com.isel.battleshipsAndroid.about.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.isel.battleshipsAndroid.R
import com.isel.battleshipsAndroid.about.model.Author
import com.isel.battleshipsAndroid.ui.TopBar
import com.isel.battleshipsAndroid.ui.theme.BattleshipsAndroidTheme


@Composable
fun AboutView(
    onBackRequest: () -> Unit,
    onSendEmailRequested: (String) -> Unit = { },
    onUrlRequested: (Uri) -> Unit = { },
    listOfAuthor: List<Author>
) {
    BattleshipsAndroidTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag("AboutView"),
            backgroundColor = MaterialTheme.colors.background,
            topBar = { TopBar(onBackRequested = { onBackRequest() }) }
        ) { innerPadding ->
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                Row(horizontalArrangement = Arrangement.Center) {
                    Text(
                        text = stringResource(id = R.string.about_battleships),
                        style = MaterialTheme.typography.h4,
                        color = MaterialTheme.colors.primaryVariant
                    )
                }
                repeat(listOfAuthor.size) {
                    AuthorView(
                        onOpenSendEmailRequested = onSendEmailRequested,
                        onOpenUrlRequested = onUrlRequested,
                        listOfAuthor[it]
                    )
                }
            }
        }
    }
}

@Composable
fun AuthorView(
    onOpenSendEmailRequested: (String) -> Unit = { },
    onOpenUrlRequested: (Uri) -> Unit = { },
    author: Author
) {
    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Bottom) {
        Text(
            text = author.name,
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.primaryVariant
        )
    }
    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Top) {
        Button(colors = ButtonDefaults.buttonColors(backgroundColor = Color(255,255,255)),onClick = {
            onOpenSendEmailRequested(author.email)
        }) {
            Image(
                painter = painterResource(id = R.drawable.email_logo),
                contentDescription = null,
                modifier = Modifier.sizeIn(25.dp, 25.dp, 50.dp, 50.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(colors = ButtonDefaults.buttonColors(backgroundColor = Color(255,255,255)),onClick = {
            onOpenUrlRequested(Uri.parse(author.github))
        }) {
            Image(
                painter = painterResource(id = R.drawable.github_logo),
                contentDescription = null,
                modifier = Modifier.sizeIn(25.dp, 25.dp, 50.dp, 50.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AboutPreview() {
    val listOfAuthor: List<Author> = listOf(
        Author(
            "Ricardo Bernardino",
            "A47283@alunos.isel.pt",
            "https://github.com/Ricardo-Bernardino-dev"
        ),
        Author("David Costa", "A45935@alunos.isel.pt", "https://github.com/A45935"),
        Author("Miguel Almeida", "A47249@alunos.isel.pt", "https://github.com/miguelalmeida2")
    )
    AboutView(
        onBackRequest = {},
        onSendEmailRequested = {},
        onUrlRequested = {},
        listOfAuthor
    )
}


