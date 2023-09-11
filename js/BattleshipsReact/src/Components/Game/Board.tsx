import React, {useEffect, useState} from "react";
import './Board.css';
import GameService from "../../services/games/game.service";

export const BOARD_ROWS = 8;
export const BOARD_COLUMNS = 8;
export const BOARD_SIZE = BOARD_COLUMNS * BOARD_ROWS;

export const squareState = {
    '_' : "sea",
    'x' : "hit",
    'o' : "miss",
    'c' : "carrier",
    'b' : "battleship",
    'd' : "destroyer",
    's' : "submarine",
    'p' : "patrol"
}

export const indexToCoordinates = (index: number) => {
    const res = index % 8 + 1
    switch (true) {
        case (index < 8):
            return 'a' + res;
        case (index < 16):
            return 'b' + res;
        case (index < 24):
            return 'c' + res;
        case (index < 32):
            return 'd' + res;
        case (index < 40):
            return 'e' + res;
        case (index < 48):
            return 'f' + res;
        case (index < 56):
            return 'g' + res;
        case (index < 64):
            return 'h' + res;
        default:
            //alert("error mapping letter");
            break;
    }
}

export const coordinatesToIndex = (coordinates) => {
    if(coordinates === undefined)
        return
    let coord = coordinates.split('')
    const sec = +coord[1] - 1; // string to int

    switch (coord[0]) {
        case ('a'):
            return sec
        case ('b'):
            return BOARD_ROWS + sec
        case ('c'):
            return 2 * BOARD_ROWS + sec
        case ('d'):
            return 3 * BOARD_ROWS + sec
        case ('e'):
            return 4 * BOARD_ROWS + sec
        case ('f'):
            return 5 * BOARD_ROWS + sec
        case ('g'):
            return 6 * BOARD_ROWS + sec
        case ('h'):
            return 7 * BOARD_ROWS + sec
        default:
            //alert("error mapping letter");
            break;
    }
}

// ordenar coodernadas de acordo com o recebido por string
export function order(board: string[]) {
    let result = []
    for(let i = 0; i < BOARD_ROWS; i++){
        result = result.concat(board.splice(0,8).reverse())
    }
    return result
}

export function getIndexes(ship) {
    let indices = [];
    let index = coordinatesToIndex(ship.position) // first index
    for (let i = 0; i < ship.length; i++) {
        indices.push(index);
        index = ship.orientation === 'vertical' ? index + BOARD_ROWS : index + 1;
    }
    return indices
}

export const Board = ({
    username,
    enemyUsername,
    gameState,
    currentlyPlacing,
    setCurrentlyPlacing,
    placeShip,
    placedShips,
    playerBoard,
    setPlayerBoard,
    refreshGameState
}) => {

    let board = order(playerBoard.toLowerCase().split(''))

    let toDisplay = currentlyPlacing ? displayPlacing() : board

    function displayPlacing() { //currentlyPlacing{name: 'carrier', length: 5, placed: null, orientation: 'horizontal', position: a1}
        let copyBoard = board.slice()

        let indexes = getIndexes(currentlyPlacing)

        if(overBoard(indexes))
            return copyBoard

        indexes.forEach((index) => {
            copyBoard[index] = currentlyPlacing.name[0]
        })

        return copyBoard
    }

    //check if placing inside board and not over placedShips
    function canPlaceCurrentShip() {
        let currentlyPlacingIndexes = getIndexes(currentlyPlacing)

        if(overBoard(currentlyPlacingIndexes))
            return false

        for (const ship of placedShips) {
            for (const index of getIndexes(ship)) {
                if (currentlyPlacingIndexes.includes(index))
                    return false
            }
        }
        return true
    }

    // check if ship goes over board on sides
    function overBoard(shipIndexes) {
        for (let i = 0 ; i < shipIndexes.length - 1; i++) {
            if(((shipIndexes[i] % BOARD_ROWS) > (shipIndexes[i + 1] % BOARD_ROWS)) || shipIndexes[i+1] > 63)
                return true
        }
        return false
    }

    let squares = toDisplay.map((square, index) => {
        if (index < 64) {
            return (
                <div
                    onClick={() => {
                        if (currentlyPlacing && canPlaceCurrentShip()) {
                            placeShip(currentlyPlacing);
                        }
                    }}
                    className={`square-${squareState[square]}`}
                    key={`square-${indexToCoordinates(index)}`}
                    id={`square-${indexToCoordinates(index)}`}
                    onMouseOver={() => {
                        if (currentlyPlacing) {
                            setCurrentlyPlacing({
                                ...currentlyPlacing,
                                position: indexToCoordinates(index)
                            });
                        }
                    }}
                />
            )
        }
    });

    return (
        <div>
            <h2 className="player-title">{username}</h2>
            <div className="board">
                {squares}
            </div>
        </div>
    );
};