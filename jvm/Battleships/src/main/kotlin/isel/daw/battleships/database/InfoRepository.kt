package isel.daw.battleships.database

import isel.daw.battleships.model.Author
import isel.daw.battleships.model.SystemInfo

interface InfoRepository {

    val systemVersion: String

    val authors : List<Author>

    fun getSystemInfo(): SystemInfo

}
