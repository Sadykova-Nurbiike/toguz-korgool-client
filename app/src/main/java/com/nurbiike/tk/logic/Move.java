package com.nurbiike.tk.logic;

public class Move {

    int fromHole;

    //fromHole - the hole that the move has to start from
    public Move(int fromHole) {
        this.fromHole = fromHole;
    }

    public String toString() {
        return "" + fromHole;
    }

    public int getFromHole() {
        return fromHole;
    }
}
