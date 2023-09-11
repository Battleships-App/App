package isel.daw.battleships.controllers.model

import isel.daw.battleships.http.infra.LinkRelation

data class Home(val links: List<Pair<LinkRelation, String>>)
