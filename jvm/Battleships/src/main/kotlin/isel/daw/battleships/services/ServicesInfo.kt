package isel.daw.battleships.services


import isel.daw.battleships.controllers.model.Home
import isel.daw.battleships.database.jdbi.TransactionManager
import isel.daw.battleships.http.Rels
import isel.daw.battleships.http.Uris
import isel.daw.battleships.model.SystemInfo
import org.springframework.stereotype.Component

@Component
class ServicesInfo(private val transactionManager: TransactionManager) : ServicesInfoInterface {

    override fun getSystemInfo(): SystemInfo {
        return transactionManager.run {
            val infoRepository = it.infoRepository
            infoRepository.getSystemInfo()
        }
    }

    override fun getHome(): Home {
        return Home(
            listOf(
                Pair(Rels.Home.ROOMS, Uris.Home.rooms().toString()),
                Pair(Rels.Home.LEADERBOARD, Uris.Home.leaderboard().toString()),
                Pair(Rels.Home.INFO, Uris.Home.info().toString()),
                Pair(Rels.Home.SIGNIN, Uris.Home.signIn().toString()),
                Pair(Rels.Home.SIGNUP, Uris.Home.signUp().toString())
            )
        )
    }
}

