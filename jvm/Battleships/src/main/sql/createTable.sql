
create table if not exists player(
    username VARCHAR(64) UNIQUE NOT NULL,
    uuid varchar(64) unique not null,
    hashPassword VARCHAR(64) NOT NULL,
    games INTEGER NOT NULL DEFAULT 0 CHECK(games >= 0),
    points INTEGER NOT NULL DEFAULT 0 CHECK(points >= 0),
    PRIMARY KEY (username)
);


CREATE table if not EXISTS game(
    uuid varchar(64) UNIQUE NOT NULL,
    player1Username VARCHAR(64) NOT NULL,
    player2Username VARCHAR(64) NOT NULL,
    gameState varchar(15) CHECK(gameState in('WAITING','BUILDING','STARTED','ENDED')),
    nextPlay varchar(15) NOT NULL CHECK(nextPlay in('PLAYER1','PLAYER2')),
    board1 VARCHAR(100) NOT NULL,
    board2 VARCHAR(100) NOT NULL,
    moves1 VARCHAR(500) NOT NULL,
    moves2 VARCHAR(500) NOT NULL,
    shotsPerRound integer NOT NULL DEFAULT 1 check(shotsPerRound >= 1),
    result varchar(15) CHECK(result in ('0','1','2')),
    PRIMARY KEY (uuid)
);

