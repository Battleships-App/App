import React, {useState} from 'react';
import "./BuildingPanel.css"

export const BuildingPanel = ({
    availableShips,
    selectShip,
    currentlyPlacing,
    startGame,
    rotateShip
}) => {

    const [buttonState, setButtonState] = useState('text');

    let shipsLeft = availableShips.map((ship) => ship.name);

    let shipBox = shipsLeft.map((shipName) => (
        <div
            id={`${shipName}-ship`}
            onClick={() => selectShip(shipName)}
            key={`${shipName}`}
            className={currentlyPlacing && currentlyPlacing.name === shipName ? 'ship placing' : 'ship'}
        >
            <div className="ship-title">{shipName}</div>
        </div>
    ));

    let fleet = (
        <div id="ship-box">
            <div className="ship-box-title"> Place Ships</div>
            {shipBox}
            <button id="rotate-button" onClick={rotateShip}>
                Rotate ship
            </button>
        </div>
    );

    let readyButton = (
        <div id="play-ready">
            <p className="game-panel">Ships are in formation.</p>
            {buttonState === 'text' ? (
                <button id="play-button" onClick={() => startGame(setButtonState)}>
                    Ready
                </button>
            ) : (<div>
                    <p className="game-panel"> You Are Ready! </p>
                    <img src="/images/greenCheck.png" alt="Ready" style={{height:"50px", width:"50px"}} />
                </div>
            )}
        </div>
    );

    let waitingMessage = (
        <div>I'm Ready!</div>
        /*
        <div id="message-ready">
            <p className="game-panel">Ships are in formation.</p>
            <button id="play-button" onClick={startGame}>
                Ready
            </button>
        </div>
         */
    );

    return ( //Passar a função de set ao estado buttonState
        <div id="available-ships">
            {availableShips.length > 0 ? fleet : readyButton }
        </div>
    );
};