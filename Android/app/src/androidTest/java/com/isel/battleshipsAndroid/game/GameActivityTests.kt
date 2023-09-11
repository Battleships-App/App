package com.isel.battleshipsAndroid.game

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.isel.battleshipsAndroid.testutils.createPreserveDefaultDependenciesComposeRule
import org.junit.Rule
import org.junit.runner.RunWith

private const val STARTUP_DELAY = 2000L

@RunWith(AndroidJUnit4::class)
class GameActivityTests{

    @get:Rule
    val testRule = createPreserveDefaultDependenciesComposeRule()

    /*
    private val delayedMockMatch: Game = createMockMatch(STARTUP_DELAY)
    private val immediateMockMatch: Game = createMockMatch()

    private val application by lazy {
        (testRule.activityRule as PreserveDefaultDependencies).testApplication
    }

    private fun createMockMatch(delayMs: Long? = null): Game = mockk(relaxed = true) {
        val localPlayer = slot<PlayerInfo>()
        coEvery { cre(capture(localPlayer), capture(challenge)) } answers {
            flow {
                if (delayMs != null)
                    delay(delayMs)
                val localMarker = getLocalPlayerMarker(localPlayer.captured, challenge.captured)
                emit(GameStarted(Game(localPlayerMarker = localMarker, board = Board())))
            }
        }
        coEvery { makeMove(any()) } returns Unit
        coEvery { forfeit() } returns Unit
    }

    private fun createMatchIntent(localPLayerStarts: Boolean): Pair<Intent, Challenge> {
        val challenge =
            if (localPLayerStarts)
                Challenge(
                    challenger = localTestPlayer,
                    challenged = otherTestPlayersInLobby.first()
                )
            else
                Challenge(
                    challenger = otherTestPlayersInLobby.first(),
                    challenged = localTestPlayer
                )
        val intent = Intent(application, GameActivity::class.java).also {
            it.putExtra(GameActivity.MATCH_INFO_EXTRA, MatchInfo(localTestPlayer, challenge))
        }
        return Pair(intent, challenge)
    }

     @Test
    fun when_game_ends_match_end_dialog_is_shown() {
        // Arrange
        val (intent, challenge) = createMatchIntent(localPLayerStarts = true)
        val localMarker = getLocalPlayerMarker(localTestPlayer, challenge)
        application.match = mockk {
            var result: BoardResult? = null
            coEvery { start(any(), any()) } answers {
                flow {
                    val game = Game(localPlayerMarker = localMarker, board = Board())
                    emit(GameStarted(game))
                    while (result != null)
                        delay(1000)
                    emit(GameEnded(game = game, winner = localMarker.other))
                }
            }
            coEvery { makeMove(any()) } returns Unit
            coEvery { forfeit() } answers { result = HasWinner(winner = localMarker.other) }
            coEvery { end() } returns Unit
        }

        ActivityScenario.launch<GameActivity>(intent).use {

            testRule
                .onNodeWithTag(ForfeitButtonTag)
                .performClick()

            testRule.waitForIdle()

            // Assert
            testRule.onNodeWithTag(MatchEndedDialogTag).assertExists()
        }
    }

    @Test
    fun when_forfeit_button_is_pressed_forfeit_is_called() {
        // Arrange
        application.match = immediateMockMatch
        val (intent, _) = createMatchIntent(localPLayerStarts = true)

        // Act
        ActivityScenario.launch<GameActivity>(intent).use {

            testRule
                .onNodeWithTag(ForfeitButtonTag)
                .performClick()

            testRule.waitForIdle()

            // Assert
            coVerify(exactly = 1) { immediateMockMatch.forfeit() }
        }
    }
     */






}