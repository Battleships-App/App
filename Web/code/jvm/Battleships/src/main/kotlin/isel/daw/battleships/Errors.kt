package isel.daw.battleships

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.testng.internal.RunInfo

class ErrorMessageModel(
    var status: Int? = null,
    var message: String? = null
)

@ControllerAdvice
class ExceptionControllerAdvice {

    class GameNotFoundException(message: String) : RuntimeException(message) {}
    class InvalidParameterException(message : String) : RuntimeException(message){}
    class GameCouldNotBeCreatedException(message: String) : RuntimeException(message) {}
    class NativeRequestDoesntExistException(message:String): RuntimeException(message){}
    class UserNotAuthorizedException(message: String) : RuntimeException(message) {}
    class WrongGameStateException(message: String) : RuntimeException(message) {}
    class NoTokenProvidedException(message: String) : RuntimeException(message) {}
    class FailedAuthenticationException(message: String) : RuntimeException(message) {}
    class AlreadyExistingDataException(message: String) : RuntimeException(message) {}
    class PlayerNotFoundException(message: String) : RuntimeException(message) {}
    class InvalidShipPlacementException(message: String) : RuntimeException(message) {}
    class InvalidMovesForSpecifiedShotRules(message: String) : RuntimeException(message) {}
    class InvalidMove(message: String) : RuntimeException(message) {}
    class NotYourTurn(message: String) : RuntimeException(message) {}
    class ParameterIsBlank(message: String) : RuntimeException(message) {}
    class RankingPagesCannotBeNegative(message: String) : RuntimeException(message) {}
    class CantSearchForBlankPlayer(message: String) : RuntimeException(message) {}


    @ExceptionHandler
    fun handleGameNotFoundException(ex: GameNotFoundException): ResponseEntity<*> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ex.message)
    }

    @ExceptionHandler
    fun handleInvalidParameterException(ex: InvalidParameterException): ResponseEntity<*>{
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ex.message)
    }


    @ExceptionHandler
    fun handleGameCouldNotBeCreatedException(ex: GameCouldNotBeCreatedException): ResponseEntity<*> {
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ex.message)
    }


    @ExceptionHandler
    fun handleUserNotFoundException(ex: UserNotAuthorizedException): ResponseEntity<*> {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ex.message)
    }

    @ExceptionHandler
    fun handleNativeRequestDoesntExistException(ex:NativeRequestDoesntExistException): ResponseEntity<*> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ex.message)
    }

    @ExceptionHandler
    fun handleNoTokenProvidedException(ex: NoTokenProvidedException): ResponseEntity<*> {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ex.message)
    }


    @ExceptionHandler
    fun handleWrongGameStateException(ex: WrongGameStateException): ResponseEntity<*> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ex.message)
    }
    @ExceptionHandler
    fun handleFailedAuthentificationException(ex: FailedAuthenticationException): ResponseEntity<*> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ex.message)
    }

    @ExceptionHandler
    fun handleAlreadyExistingDataException(ex: AlreadyExistingDataException): ResponseEntity<*> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ex.message)
    }

    @ExceptionHandler
    fun handlePlayerNotFoundException(ex: PlayerNotFoundException): ResponseEntity<*> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ex.message)
    }


    @ExceptionHandler
    fun handleInvalidShipPlacementException(ex: InvalidShipPlacementException): ResponseEntity<*> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ex.message)
    }
    @ExceptionHandler
    fun handleInvalidMovesForSpecifiedShotRulesException(ex: InvalidMovesForSpecifiedShotRules): ResponseEntity<*> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ex.message)
    }
    @ExceptionHandler
    fun handleInvalidMoveException(ex: InvalidMove): ResponseEntity<*> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ex.message)
    }

    @ExceptionHandler
    fun handleNotYourTurnException(ex: NotYourTurn): ResponseEntity<*> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ex.message)
    }

    @ExceptionHandler
    fun handleBlankParameterException(ex: ParameterIsBlank): ResponseEntity<*> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ex.message)
    }

    @ExceptionHandler
    fun handleRankingPagesCannotBeNegativeException(ex: RankingPagesCannotBeNegative): ResponseEntity<*> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ex.message)
    }

    @ExceptionHandler
    fun handleCantSearchForBlankPlayerException(ex: CantSearchForBlankPlayer): ResponseEntity<*> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ex.message)
    }

}
