package isel.daw.battleships.database.jdbi

import isel.daw.battleships.AUTHORS
import isel.daw.battleships.VERSION
import isel.daw.battleships.database.InfoRepository
import isel.daw.battleships.model.Author
import isel.daw.battleships.model.SystemInfo
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.Handle
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository


class JdbiInfoRepository(private val handle: Handle) : InfoRepository {

    override val systemVersion: String = VERSION

    override val authors: List<Author> = AUTHORS

    override fun getSystemInfo(): SystemInfo {
        return SystemInfo(systemVersion, authors)
    }
}


