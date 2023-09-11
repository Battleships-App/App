package isel.daw.battleships.services

import isel.daw.battleships.controllers.model.Home
import isel.daw.battleships.model.SystemInfo

interface ServicesInfoInterface {

    /**
     * SystemInfo is an object that contains all information about app Authors and current version
     * @return the SystemInfo
     */
    fun getSystemInfo(): SystemInfo

    fun getHome(): Home
}
