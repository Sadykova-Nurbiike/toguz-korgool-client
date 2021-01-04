package com.nurbiike.tk;

import com.nurbiike.tk.logic.GameStateSingle;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class GameStateSingleTest {

    @Test
    public void testValue() {
        GameStateSingle gameState = new GameStateSingle();
        assertThat(gameState).isInstanceOf(GameStateSingle.class);

        gameState.kazan = new int[]{20, 33};

        int expectedValue = 130;
        assertThat(gameState.value()).isEqualTo(expectedValue);
    }

    @Test
    public void testNextPossibleMoves() {
        GameStateSingle gameState = new GameStateSingle();
        assertThat(gameState).isInstanceOf(GameStateSingle.class);

        gameState.board = new int[]{0, 21, 11, 11, 0, 11, 10, 0, 9, 0, 8, 0, 0, 0, 9, 0, 0, 0};

        assertThat(gameState.nextLegalMoves()).hasSize(2);
    }

    @Test
    public void testSearchAlphaBeta() {
        GameStateSingle gameState = new GameStateSingle();
        assertThat(gameState).isInstanceOf(GameStateSingle.class);

        int result = gameState.minimaxAlphaBeta(Integer.MIN_VALUE, Integer.MAX_VALUE, 5);
        int bestMove = gameState.bestMove.getFromHole();

        assertThat(result).isEqualTo(-30);
        assertThat(bestMove).isEqualTo(11);
    }
}
