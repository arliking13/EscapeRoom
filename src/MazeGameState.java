

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class MazeGameState {

    public Player player = new Player(); // Uses your default constructor
    public List<Enemy> enemies = new ArrayList<>();
    public boolean gameWon = false;

    public MazeGameState() {
        reset();
    }

    public void reset() {
        player.setRow(1);
        player.setCol(1);
        player.setHealth(3);
        player.setHasKey(false);
        player.setWon(false);

        enemies.clear();

        // ✅ Add multiple enemies with different positions
       
        enemies.add(new Enemy(6, 8));    // Mid-upper area, won’t block early path
        enemies.add(new Enemy(13, 6));   // Moves in the middle of the maze
        enemies.add(new Enemy(16, 18));  // Closer to goal, light challenge

    }

    // Optional: only use this if you're not using MazePanel's own move logic
    public void movePlayer(int dRow, int dCol) {
        player.move(dRow, dCol);
        checkGoal();
    }

    public void checkGoal() {
        // Customize this to match your maze's goal tile
        if (player.getRow() == 18 && player.getCol() == 23 && player.hasKey()) {
            player.setWon(true);
            gameWon = true;

            // ✅ Notify puzzle tracker that maze is solved
            PuzzleTracker.reportMazeSolved();
        }
    }

    

    public boolean isNearEnemy(double range) {
        Point playerPos = new Point(player.getCol(), player.getRow());
        for (Enemy e : enemies) {
            Point enemyPos = new Point(e.getCol(), e.getRow());
            if (playerPos.distance(enemyPos) < range) {
                return true;
            }
        }
        return false;
    }
}
