package com.example.labyrinth;

public class Enemy {
    public int row, col;

    public Enemy(int r, int c) {
        this.row = r;
        this.col = c;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
