package com.nurbiike.tk.logic;

import java.lang.reflect.Array;

//GameState consists of a Toguz Korgool game-board and methods to play
public abstract class GameState {

    public boolean maxTurn;

    public int board[] = new int[18];
    public int kazan[] = new int[2]; //kazan of player 1 - at index 0, player 2 - at index 1
    public int tuz[] = new int[2];
    protected static int STARTBALLS = 9;
    protected int your_number;


    public GameState() {
        this.your_number = 1; // in single mode user is always player 1
        for (int i = 0; i < 18; i++) {
            board[i] = STARTBALLS;
        }
        for (int i = 0; i < 2; i++) {
            kazan[i] = 0;
            tuz[i] = -1; // -1 denotes no tuz
        }
    }

    public GameState(int playerNumber) {
        this();
        this.your_number = playerNumber;
    }

    /**
     * Makes a given move in a game.
     *
     * @param m the move to be made.
     * @return sound to play
     */
     /*it checks the holes that belong to the player using the playerNumber
    if playerNumber = 1, then the player has holes 9 - 17
    if playerNumber = 2, then the player has holes 0 - 8*/
    public MoveSound makeMove(Move m) {

        //Sound
        MoveSound moveSound = MoveSound.MOVE;

        Move tkm = (Move) m;
        int fromHole = tkm.fromHole;
        int iniFromHole = fromHole;
        int lastHole = -1;

        int ballsToMove = this.board[fromHole];
        this.board[fromHole] = 0;

        /*The player starting the game, takes all balls from any hole on his side,
        and starting with
        that hole places one ball in each hole anticlockwise.
        If the move starts from
        the hole, where there is only one ball, so the ball is moving to the next
        hole, accordingly the
        previous hole becomes empty.*/
        if (ballsToMove > 1) {
            for (int i = ballsToMove; i > 0; i--) {
                fromHole = fromHole % 18;
                this.board[fromHole] += 1;
                lastHole = fromHole;
                fromHole++;
            }
        } else {
            fromHole++;
            lastHole = fromHole % 18;
            this.board[lastHole] += 1;
        }

        /*if the last ball lands into one of the opponent's hole, and the
        number of balls in this hole would be even, so the move is considered
        resulting and the player takes all balls from this hole to his kazan.*/
        if (9 <= iniFromHole && iniFromHole <= 17 && 0 <= lastHole && lastHole <= 8 && this.tuz[0] != lastHole) {
            if (this.board[lastHole] % 2 == 0) {
                this.kazan[0] += this.board[lastHole];
                if (this.board[lastHole] > 15) {
                    moveSound = MoveSound.PLUS15GAIN;
                } else {
                    moveSound = MoveSound.GAIN;
                }
                this.board[lastHole] = 0;
            } else if (this.tuz[0] == -1 && this.board[lastHole] == 3 && lastHole != 8 && (this.tuz[1] == -1 || (this.tuz[1] != -1 && this.tuz[1] - 9 != lastHole))) {
                this.tuz[0] = lastHole;
                moveSound = MoveSound.TUZ;
            }
        } else if (0 <= iniFromHole && iniFromHole <= 8 && 9 <= lastHole && lastHole <= 17 && this.tuz[1] != lastHole) {
            if (this.board[lastHole] % 2 == 0) {
                this.kazan[1] += this.board[lastHole];
                if (this.board[lastHole] > 15) {
                    moveSound = MoveSound.PLUS15GAIN;
                } else {
                    moveSound = MoveSound.GAIN;
                }
                this.board[lastHole] = 0;
            } else if (this.tuz[1] == -1 && this.board[lastHole] == 3 && lastHole != 17 && (this.tuz[0] == -1 || (this.tuz[0] != -1 && this.tuz[0] + 9 != lastHole))) {
                this.tuz[1] = lastHole;
                moveSound = MoveSound.TUZ;
            }
        }

        //Add all balls in tuz to corresponding kazan
        for (int i = 0; i < 2; i++) {
            if (this.tuz[i] != -1) {
                this.kazan[i] += this.board[this.tuz[i]];
                this.board[this.tuz[i]] = 0;
            }
        }

        // When one player no longer has ball in any of his holes, the game ends.
        // The other player moves all remaining balls to his kazan, and the player with
        // the most ball in her kazan wins.
        if (9 <= iniFromHole && iniFromHole <= 17) { //player 1

            boolean opponentsHolesEmpty = true;
            for (int i = 0; i <= 8; i++) { //2nd player holes
                if (this.board[i] > 0) {
                    opponentsHolesEmpty = false;
                    break;
                }
            }

            if (opponentsHolesEmpty) {
                for (int i = 9; i <= 17; i++) {
                    this.kazan[0] += this.board[i];
                    this.board[i] = 0;
                }
            }

        } else if (0 <= iniFromHole && iniFromHole <= 8) { //player 2

            boolean opponentsHolesEmpty = true;
            for (int i = 9; i <= 17; i++) { //1st player holes
                if (this.board[i] > 0) {
                    opponentsHolesEmpty = false;
                    break;
                }
            }

            if (opponentsHolesEmpty) {
                for (int i = 0; i <= 8; i++) {
                    this.kazan[1] += this.board[i];
                    this.board[i] = 0;
                }
            }

        }

        this.maxTurn = !this.maxTurn;

        return moveSound;

    }

    public boolean gameOver() {
        return (kazan[0] > 9 * STARTBALLS || kazan[1] > 9 * STARTBALLS)
                || (kazan[0] == 9 * STARTBALLS && kazan[1] == 9 * STARTBALLS);
    }


    public boolean isLegalMove(int fromHole) {
        return board[fromHole] != 0 && fromHole != tuz[0] && fromHole != tuz[1];
    }


    public int getCnt(int holeNum) {
        return board[holeNum];
    }

    /*Returns -1 if opponent is winner
      1 if player is winner
      0 if game is draw*/
    public int whoIsWinner() {

        for (int i = 0; i <= 8; i++) {
            this.kazan[1] += this.board[i];
            this.board[i] = 0;
        }

        for (int i = 9; i <= 17; i++) {
            this.kazan[0] += this.board[i];
            this.board[i] = 0;
        }

        int playersScore = this.kazan[this.your_number - 1];
        int opponentsScore = this.kazan[this.your_number % 2];
        if (playersScore > opponentsScore) {
            return 1;
        } else if (opponentsScore > playersScore) {
            return -1;
        } else {
            return 0;
        }
    }

}