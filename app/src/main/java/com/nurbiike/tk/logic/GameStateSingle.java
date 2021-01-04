package com.nurbiike.tk.logic;

import java.util.ArrayList;
import java.util.List;

public class GameStateSingle extends GameState implements Cloneable {

    public Move bestMove;

    public GameStateSingle() {
        super();
    }

    /**
     * Makes a move from the given hole number.
     */
    protected void makeMove(int hole) {
        makeMove(new Move(hole));
    }


    /**
     * Evaluates the value of the current game position/status.
     *
     * @return Returns an integer value of the current game position/status.
     */
    public int value() {
        int currentValue = 0;
        currentValue += 10 * (kazan[1] - kazan[0]);
        return currentValue;
    }


    /**
     * Copy the current game state. This method is important for
     * minimaxAlphaBeta method as this method remembers the current game
     * state before it actually makes the move.
     *
     * @return Returns the current game state.
     */
    protected GameState copy() {
        GameStateSingle gs = new GameStateSingle();
        gs.maxTurn = maxTurn;
        gs.board = board.clone();
        gs.kazan = kazan.clone();
        gs.tuz = tuz.clone();
        return gs;
    }

    /**
     * Generates next possible moves from current game state depending on
     * whose turn.
     *
     * @return Returns a list with all possible moves from current game state.
     */
    public List<Move> nextLegalMoves() {
        List<Move> nextLegalMovesList = new ArrayList<Move>();
        if (!gameOver()) {
            if (maxTurn) {
                for (int holeNumber = 0; holeNumber <= 8; holeNumber++)
                    if (board[holeNumber] != 0)
                        nextLegalMovesList.add(new Move(holeNumber));
            } else {
                for (int holeNumber = 9; holeNumber <= 17; holeNumber++)
                    if (board[holeNumber] != 0)
                        nextLegalMovesList.add(new Move(holeNumber));
            }
        }
        return nextLegalMovesList;
    }

    /**
     * Finds the best move from current game state using alpha-beta
     * algorithm.
     *
     * @param depth the maximum depth alpha-beta algorithm has to search.
     * @return Returns the best move, which is found by the alpha-beta.
     */
    public Move getBestMove(int depth) {
        bestMove = null;
        minimaxAlphaBeta(Integer.MIN_VALUE, Integer.MAX_VALUE, depth);
        return bestMove;
    }

    /**
     * Search-algorithm which searches the best move for the computer
     *
     * @param alpha the best value for max.
     * @param beta  the best value for min.
     * @param depth the maximum depth that how many game states the
     *              computer has to think ahead.
     * @return Returns the value of given game state.
     */
    public int minimaxAlphaBeta(int alpha, int beta, int depth) {
        List<Move> nextLegalMovesList = nextLegalMoves();
        if (depth <= 0 || nextLegalMovesList == null || nextLegalMovesList.isEmpty()) {
            return value();
        }
        for (Move move : nextLegalMovesList) {
            GameStateSingle gs = (GameStateSingle) copy();
            gs.makeMove(move);
            int value = gs.minimaxAlphaBeta(alpha, beta, depth - 1);
            if (maxTurn && value > alpha) {
                alpha = value;
                if (alpha >= beta) {
                    return alpha;
                }
                bestMove = move;
            } else if (!maxTurn && value < beta) {
                beta = value;
                if (alpha >= beta) {
                    return beta;
                }
                bestMove = move;
            }
        }
        return maxTurn ? alpha : beta;
    }

}
