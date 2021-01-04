package com.nurbiike.tk;

import com.nurbiike.tk.logic.GameStateMultiPlayer;
import com.nurbiike.tk.logic.Move;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class GameStateMultiPlayerTest {

    @Test
    public void testMove_BallsGained() {
        GameStateMultiPlayer gameState = new GameStateMultiPlayer(1);
        assertThat(gameState).isInstanceOf(GameStateMultiPlayer.class);

        gameState.makeMove(new Move(17));

        int[] expectedKazan = {10, 0};
        int[] expectedBoard = {10, 10, 10, 10, 10, 10, 10, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 1};
        int[] expectedTuz = {-1, -1};
        assertThat(gameState.kazan).isEqualTo(expectedKazan);
        assertThat(gameState.board).isEqualTo(expectedBoard);
        assertThat(gameState.tuz).isEqualTo(expectedTuz);
    }

    @Test
    public void testMove_NoBallsGained() {
        GameStateMultiPlayer gameState = new GameStateMultiPlayer(1);
        assertThat(gameState).isInstanceOf(GameStateMultiPlayer.class);

        gameState.makeMove(new Move(17));

        int[] expectedKazan = {10, 0};
        assertThat(gameState.kazan).isEqualTo(expectedKazan);

        gameState.makeMove(new Move(15));

        int[] expectedBoard = {11, 11, 11, 11, 11, 11, 10, 0, 9, 9, 9, 9, 9, 9, 9, 1, 10, 2};
        int[] expectedTuz = {-1, -1};
        assertThat(gameState.kazan).isEqualTo(expectedKazan);
        assertThat(gameState.board).isEqualTo(expectedBoard);
        assertThat(gameState.tuz).isEqualTo(expectedTuz);
    }

    @Test
    public void testMove_TuzGained() {
        GameStateMultiPlayer gameState = new GameStateMultiPlayer(1);
        assertThat(gameState).isInstanceOf(GameStateMultiPlayer.class);

        gameState.makeMove(new Move(17));
        gameState.board = new int[]{2, 21, 11, 11, 11, 11, 10, 0, 9, 9, 9, 9, 9, 9, 9, 1, 10, 1};
        gameState.makeMove(new Move(17));

        int[] expectedKazan = {13, 0};
        int[] expectedBoard = {0, 21, 11, 11, 11, 11, 10, 0, 9, 9, 9, 9, 9, 9, 9, 1, 10, 0};
        int[] expectedTuz = {0, -1};
        assertThat(gameState.kazan).isEqualTo(expectedKazan);
        assertThat(gameState.board).isEqualTo(expectedBoard);
        assertThat(gameState.tuz).isEqualTo(expectedTuz);
    }

    @Test
    public void testGameOver_True_WithWinner() {
        GameStateMultiPlayer gameState = new GameStateMultiPlayer(1);
        assertThat(gameState).isInstanceOf(GameStateMultiPlayer.class);

        gameState.kazan = new int[]{82, 12};
        assertThat(gameState.gameOver()).isTrue();
    }

    @Test
    public void testGameOver_True_Draw() {
        GameStateMultiPlayer gameState = new GameStateMultiPlayer(1);
        assertThat(gameState).isInstanceOf(GameStateMultiPlayer.class);

        gameState.kazan = new int[]{81, 81};
        assertThat(gameState.gameOver()).isTrue();
    }

    @Test
    public void testGameOver_False() {
        GameStateMultiPlayer gameState = new GameStateMultiPlayer(1);
        assertThat(gameState).isInstanceOf(GameStateMultiPlayer.class);

        gameState.board = new int[]{34, 67};
        assertThat(gameState.gameOver()).isFalse();
    }

    @Test
    public void testIsLegalMove_True() {
        GameStateMultiPlayer gameState = new GameStateMultiPlayer(1);
        assertThat(gameState).isInstanceOf(GameStateMultiPlayer.class);

        gameState.board = new int[]{0, 21, 11, 11, 0, 11, 10, 0, 9, 9, 9, 9, 9, 9, 9, 1, 10, 0};
        assertThat(gameState.isLegalMove(10)).isTrue();

        gameState.tuz = new int[]{3, 16};
        assertThat(gameState.isLegalMove(5)).isTrue();
    }

    @Test
    public void testIsLegalMove_False() {
        GameStateMultiPlayer gameState = new GameStateMultiPlayer(1);
        assertThat(gameState).isInstanceOf(GameStateMultiPlayer.class);

        gameState.board = new int[]{0, 21, 11, 11, 0, 11, 10, 0, 9, 9, 9, 9, 9, 9, 9, 1, 10, 0};
        assertThat(gameState.isLegalMove(4)).isFalse();

        gameState.tuz = new int[]{3, 16};
        assertThat(gameState.isLegalMove(3)).isFalse();
    }

    @Test
    public void testWhoIsWinner() {
        GameStateMultiPlayer gameState = new GameStateMultiPlayer(1);
        assertThat(gameState).isInstanceOf(GameStateMultiPlayer.class);

        gameState.kazan = new int[]{89, 75};
        assertThat(gameState.whoIsWinner()).isEqualTo(1);

        gameState.kazan = new int[]{81, 81};
        assertThat(gameState.whoIsWinner()).isEqualTo(0);

        gameState.kazan = new int[]{80, 82};
        assertThat(gameState.whoIsWinner()).isEqualTo(-1);
    }


}
