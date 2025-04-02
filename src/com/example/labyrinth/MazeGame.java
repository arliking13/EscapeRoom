package com.example.labyrinth;

import javax.swing.JFrame;

public class MazeGame extends JFrame {

    public static void main(String[] args) {
        new MazeGame();
    }

    public MazeGame() {
        setTitle("Horror Labyrinth - Multi-File Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        MazeGameState gameState = new MazeGameState();
        MazePanel panel = new MazePanel(gameState);

        add(panel);
        pack();

        setLocationRelativeTo(null);
        setVisible(true);

        panel.requestFocusInWindow(); // ensure key events are captured
    }
}
