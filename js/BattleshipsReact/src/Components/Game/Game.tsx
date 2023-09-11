import React, {useEffect, useState} from "react";
import {GamePanel} from "./GamePanel";
import {BuildingPanel} from "./BuildingPanel"
import {Board, BOARD_ROWS, coordinatesToIndex, getIndexes, indexToCoordinates, order} from "./Board";
import './Game.css';
import gameService from "../../services/games/game.service";
import {EnemyBoard} from "./EnemyBoard"
import GameService from "../../services/games/game.service";
import { useLocation } from 'react-router-dom';

/** BOARD STRING COORDINATES
 "a8a7a6a5a4a3a2a1_b8b7b6b5b4b3b2b1_c8c7c6c5c4c3c2c1_d8d7d6d5d4d3d2d1_e8e7e6e5e4e3e2e1_f8f7f6f5f4f3f2f1_g8g7g6g5g4g3g2g1_h8h7h6h5h4h3h2h1"      */

const emptyBoard = '________________________________________________________________'
//const enemyBoardtest = '__sss___xxxx___pp____ooo____ddd___xxx_____bbbb__oooooo___ccccc__'
const AVAILABLE_SHIPS = [
    {
        name: 'carrier',
        length: 5,
        placed: null,
    },
    {
        name: 'battleship',
        length: 4,
        placed: null,
    },
    {
        name: 'destroyer',
        length: 3,
        placed: null,
    },
    {
        name: 'submarine',
        length: 3,
        placed: null,
    },
    {
        name: 'patrolboat',
        length: 2,
        placed: null,
    },
];

export const Game = ({
    uuid,
    player1,
    player2,
    spr,
    setCurrentMatch,
    whoAmI
}) => {

    const [gameState, setGameState] = useState('WAITING');
    const [winner, setWinner] = useState(null);

    const [currentlyPlacing, setCurrentlyPlacing] = useState(null);
    const [placedShips, setPlacedShips] = useState([]);
    const [availableShips, setAvailableShips] = useState(AVAILABLE_SHIPS);

    const [playerBoard, setPlayerBoard] = useState(emptyBoard);
    const [enemyBoard, setEnemyBoard] = useState(emptyBoard);
    const [turn, setTurn] = useState(null);

    const [playerHits, setPlayerHits] = useState(0);

    useEffect(() => {
        const interval = setInterval( async () => {
            await refreshGame()
        }, 500);
        return () => clearInterval(interval);
    },[turn])

    // tranlate placedShips into service request type ["a1c a2c a3c a4c a5c","c1b c2b c3b c4b","e1d e2d e3d","g1s g2s g3s","d7p e7p"]
    function getShipsPlacement() {
        let result = []
        let positions = []
        placedShips.forEach( ship => {
            getIndexes(ship).forEach(index => {
                positions.push(indexToCoordinates(index) + ship.name[0])
            })
            result.push(positions.join(' '))
            positions.length = 0 // clear array
        })
        return result
    }

    // tranlate indices of placement to new board string
    const placementsToString = () => {
        let nboard = emptyBoard.split('')
        placedShips.forEach( ship => {
            getIndexes(ship).forEach(index => {
                nboard[index] = ship.name[0]
            })
        })
        return order(nboard).join('')
    }

    const gameStart = async (setter) => {
        //set enemy ships on second board
        console.log(getShipsPlacement())
        await gameService.buildBoard(uuid, getShipsPlacement())
        setter('image');
    };

    const selectShip = (shipName) => {
        let shipId = availableShips.findIndex((ship) => ship.name === shipName);
        const shipToPlace = availableShips[shipId];

        setCurrentlyPlacing({
            ...shipToPlace,
            orientation: 'horizontal',
            position: 'a1',
        });
    };

    useEffect(() => {
        setPlayerBoard(placementsToString)
    }, [placedShips])

    const placeShip = (currentlyPlacing) => {
        setPlacedShips([
            ...placedShips,
            {
                ...currentlyPlacing,
                placed: true,
            },
        ]);

        setAvailableShips((previousShips) =>
            previousShips.filter((ship) => ship.name !== currentlyPlacing.name)
        );

        setCurrentlyPlacing(null);
    };

    const rotateShip = () => {
        if (currentlyPlacing != null) {
            setCurrentlyPlacing({
                ...currentlyPlacing,
                orientation:
                    currentlyPlacing.orientation === 'vertical' ? 'horizontal' : 'vertical',
            });
        }
    };

    const refreshGame = async () => {
        const data = await GameService.refreshedGameState(uuid)

        setGameState(data.gameState)
        setTurn(data.turn)

        if (gameState === 'ENDED'){
            if (data.result == 1 && whoAmI == 'PLAYER1' || data.result == 2 && whoAmI == 'PLAYER2')
                setWinner(player1)
            else
                setWinner(player2)

            return
        }

        if(gameState === 'WAITING' || gameState === 'BUILDING')
            return

        if (whoAmI === 'PLAYER1') {
            setPlayerBoard(data.board1)
            setEnemyBoard(data.board2)
        } else {
            setPlayerBoard(data.board2)
            setEnemyBoard(data.board1)
        }
    }

    const makeMove = async (shots) => {
        await GameService.makeMove(uuid, shots)
    }

    const handleForfeit = async () => {
        //TODO - distingir se saiu por desistência ou por estar farto de esperar
        await gameService.forfeitMatch(localStorage.getItem("currentMatch"))
        localStorage.removeItem("currentMatch");
        localStorage.removeItem("currentMatchP2");
        setCurrentMatch(null);
    };

    return (
        <section id="game-screen">
            {gameState !== 'BUILDING' ? (
                <GamePanel
                    successfulHits={playerHits}
                    winner={winner}
                />
            ) : (
                <BuildingPanel
                    availableShips={availableShips}
                    selectShip={selectShip}
                    currentlyPlacing={currentlyPlacing}
                    startGame={gameStart}
                    rotateShip={rotateShip}
                />
            )}
            {/* Player board */}
            <div style={{ display: 'flex', alignItems: 'center', height: '100%' }}>
                <div style={{ display: 'flex', justifyContent: 'center', flexDirection: 'row' }}>
            <Board
                username={player1}
                enemyUsername={player2}
                gameState={gameState}
                currentlyPlacing={currentlyPlacing}
                setCurrentlyPlacing={setCurrentlyPlacing}
                placeShip={placeShip}
                placedShips={placedShips}
                playerBoard={playerBoard}
                setPlayerBoard={setPlayerBoard}
                refreshGameState={() => refreshGame}
            />
                    <h3 style={{fontSize: '1.5em'}}>
                        {turn === whoAmI ? (
                            <div style={{position: 'relative'}}>
                                <img src={'/images/missile-gif.gif'} alt="Opponent's Turn" />
                                <h3 style={{position: 'absolute', top: '100%', right: '-15%', textAlign: 'center'}}> Your Turn </h3>
                            </div>

                        ) : (
                            <div style={{position: 'relative'}}>
                                <img src={'/images/loading-gif.gif'} alt="Opponent's Turn" />
                                <h3 style={{position: 'absolute', top: '36%', right: '-30%' ,textAlign: 'center'}}> Enemy's Turn </h3>
                            </div>
                        )}
                    </h3>
                        <EnemyBoard
                enemyUsername={player2}
                gameState={gameState}
                playerBoard={enemyBoard}
                makeMove={(shots) => makeMove(shots)}
                myTurn={() => { return turn === whoAmI }}
                hitIncrease={() => setPlayerHits(p => p + 1)}
            />
                </div>
            </div>

            <button id="play-button" onClick={handleForfeit}>Leave Match</button>
        </section>
    );
};