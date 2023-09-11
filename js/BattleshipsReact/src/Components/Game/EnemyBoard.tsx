import React from "react";
import {indexToCoordinates, order, squareState} from "./Board";
import GameService from "../../services/games/game.service";
import './EnemyBoard.css'

export const EnemyBoard = ({
    enemyUsername,
    gameState,
    playerBoard,
    makeMove,
    myTurn,
    hitIncrease
}) => {
    let board = order(playerBoard.toLowerCase().split(''))

    let squareStateToDisplay;

    let squares = board.map((square, index) => {
        // hide enemy board squares until move is made
        if(squareState[square] === 'hit' || squareState[square] === 'miss')
            squareStateToDisplay = squareState[square]
        else
            squareStateToDisplay = 'sea'

        return (
            <div
                onClick={() => {
                    if (gameState === 'STARTED' && myTurn()) {
                        if (makeMove([indexToCoordinates(index)]))
                            hitIncrease()
                    }
                }}
                className={`square-${squareStateToDisplay}`}
                key={`square-${indexToCoordinates(index)}`}
                id={`square-${indexToCoordinates(index)}`}
            />
        )

    });

    return (
        <div>
            <h2 className="player-title">{enemyUsername}</h2>
            <div className="board">
                {squares}
            </div>

        </div>
    );
}