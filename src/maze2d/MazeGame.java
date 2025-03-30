// Updated MazeGame.java
package maze2d;

import javax.swing.JFrame;

public class MazeGame extends JFrame {
    private MazePanel panel;

    public static void main(String[] args) {
        new MazeGame();
    }

    public MazeGame() {
        setTitle("Horror Labyrinth - Multi-File Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        panel = new MazePanel();
        add(panel);
        pack();

        setLocationRelativeTo(null);
        setVisible(false); // Don't show as separate window when used in 3D

        panel.requestFocusInWindow();
    }

    public MazePanel getMazePanel() {
        return panel;
    }
}