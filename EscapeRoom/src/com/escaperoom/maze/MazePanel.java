package com.escaperoom.maze;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;

public class MazePanel extends JPanel implements KeyListener {

    // Maze dimensions
    private static final int ROWS = 20;
    private static final int COLS = 25;
    private static final int TILE_SIZE = 32;

    // Extra HUD space at the bottom
    private static final int HUD_HEIGHT = 64;

    // Font for drawing emojis
    private static final Font FONT = new Font("Monospaced", Font.BOLD, 24);

    // Maze tile definitions
    // 1=Wall, 2=Key, 3=Exit, 4=Trap, 5=LockedDoor
    private int[][] mazeData = {
        // same data as before
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,1},
        {1,0,1,1,1,1,1,1,1,0,0,0,0,0,1,0,1,1,1,1,1,1,1,0,1},
        {1,0,1,0,0,0,0,0,1,0,0,0,4,0,1,0,0,0,0,0,0,0,1,0,1},
        {1,0,1,0,1,1,4,0,1,0,1,1,1,0,1,0,1,1,1,4,1,0,1,0,1},
        {1,0,1,0,1,0,0,0,1,0,1,0,0,0,1,0,0,0,0,0,1,0,1,0,1},
        {1,0,1,0,1,0,1,1,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},
        {1,0,0,0,1,0,0,0,4,0,1,0,1,0,1,0,1,0,1,0,4,0,1,0,1},
        {1,1,1,0,1,0,1,1,1,0,1,0,1,0,1,0,1,0,1,1,1,0,1,0,1},
        {1,0,0,0,1,0,0,0,0,0,1,0,1,0,1,0,0,0,0,0,1,0,1,0,1},
        {1,0,1,1,1,1,1,1,1,0,1,0,1,0,1,1,1,1,1,0,1,0,1,0,1},
        {1,0,1,0,0,0,0,0,1,0,5,0,4,0,1,0,0,0,0,0,1,0,1,0,1},
        {1,0,1,0,1,1,1,0,1,0,1,0,1,0,1,0,1,1,1,0,1,0,1,0,1},
        {1,0,1,0,1,0,0,0,1,0,1,0,1,0,1,0,0,0,0,0,1,0,1,0,1},
        {1,0,1,0,1,0,1,1,1,0,1,0,1,0,1,1,1,1,1,0,1,0,1,0,1},
        {1,0,4,0,1,0,0,0,1,0,1,0,1,0,4,0,0,0,1,0,1,0,1,0,1},
        {1,0,0,0,1,1,1,0,1,0,1,0,1,0,1,0,1,2,1,0,1,0,1,0,1},
        {1,0,1,1,1,0,1,0,1,0,4,0,4,0,1,0,1,0,1,0,1,0,1,0,1},
        {1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,3,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    // We define "startRow" and "startCol" so resetPlayer() can use them
    private final int startRow = 1;
    private final int startCol = 1;

    // Player & Enemies
    private Player player;
    private List<Enemy> enemies;

    // Animation
    private int animationFrame = 0;

    public MazePanel() {
        int totalHeight = ROWS * TILE_SIZE + HUD_HEIGHT;
        setPreferredSize(new Dimension(COLS * TILE_SIZE, totalHeight));
        setBackground(Color.BLACK);

        // Create the Player at row=1, col=1
        player = new Player(startRow, startCol);

        enemies = new ArrayList<>();
        enemies.add(new Enemy(3, 9));
        enemies.add(new Enemy(7, 3));
        enemies.add(new Enemy(9, 15));

        addKeyListener(this);
        setFocusable(true);

        // Timer for toggling animation frames every 300ms
        new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationFrame = (animationFrame + 1) % 2;
                // update player's celebration timer
                player.updateAnimation();
                repaint();
            }
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setFont(FONT);
        FontMetrics fm = g.getFontMetrics();
        int maxCharWidth = Emotes.measureMaxEmojiWidth(fm);

        drawMaze(g, fm, maxCharWidth);
        drawEnemies(g, fm, maxCharWidth);

        // if not won
        if (!player.hasWon()) {
            drawPlayer(g, fm, maxCharWidth);
        }

        // top-left HUD
        g.setColor(Color.WHITE);
        g.drawString("Health: " + player.getHealth(), 10, 20);
        g.drawString("Key: " + (player.hasKey() ? "Yes" : "No"), 10, 40);

        if (player.hasWon()) {
            g.setColor(Color.WHITE);
            g.drawString("You found a door...", TILE_SIZE * 5, TILE_SIZE * 8);
            g.drawString("Something feels off.", TILE_SIZE * 5, TILE_SIZE * 9);
            g.drawString("Close this window if you dare.", TILE_SIZE * 5, TILE_SIZE * 10);
        }

        // draw small status box in bottom-left
        drawStatusBox(g, fm, maxCharWidth);

        // optional "doom face" in bottom center
        drawDoomHUD(g, fm, maxCharWidth);
    }

    private void drawMaze(Graphics g, FontMetrics fm, int charWidth) {
        int charHeight = fm.getAscent();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                int tile = mazeData[r][c];
                String ch;
                Color color;

                switch(tile) {
                    case 1: ch = "▓"; color = Color.DARK_GRAY; break;
                    case 2: ch = "🔑"; color = new Color(128, 0, 128); break;
                    case 3: ch = "🚪"; color = Color.GREEN; break;
                    case 4: ch = "💀"; color = Color.RED; break;
                    case 5: ch = "🔒"; color = new Color(70,70,70); break;
                    default: ch = " "; color = new Color(30,30,30); break;
                }

                int x = c * TILE_SIZE;
                int y = r * TILE_SIZE;

                g.setColor(Color.BLACK);
                g.fillRect(x, y, TILE_SIZE, TILE_SIZE);

                g.setColor(color);
                int textX = x + (TILE_SIZE - charWidth) / 2;
                int textY = y + (TILE_SIZE + charHeight) / 2;
                g.drawString(ch, textX, textY);
            }
        }
    }

    private void drawEnemies(Graphics g, FontMetrics fm, int charWidth) {
        int charHeight = fm.getAscent();
        for (Enemy e : enemies) {
            double dist = distance(player.getRow(), player.getCol(), e.row, e.col);
            String[] frames = (dist < 3) ? Emotes.enemyFramesAttack : Emotes.enemyFramesIdle;
            String enemyEmoji = frames[animationFrame];

            int x = e.col * TILE_SIZE;
            int y = e.row * TILE_SIZE;

            g.setColor(Color.BLACK);
            g.fillRect(x, y, TILE_SIZE, TILE_SIZE);

            g.setColor(new Color(30,30,30));
            int textX = x + (TILE_SIZE - charWidth) / 2;
            int textY = y + (TILE_SIZE + charHeight) / 2;
            g.drawString(" ", textX, textY);

            g.setColor(Color.MAGENTA);
            g.drawString(enemyEmoji, textX, textY);
        }
    }

    private void drawPlayer(Graphics g, FontMetrics fm, int charWidth) {
        int charHeight = fm.getAscent();
        boolean nearEnemy = isNearEnemy(2.0);

        // get player's current frame
        String playerEmoji = player.getCurrentFrame(animationFrame, nearEnemy);

        int x = player.getCol() * TILE_SIZE;
        int y = player.getRow() * TILE_SIZE;

        g.setColor(Color.BLACK);
        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);

        g.setColor(new Color(30,30,30));
        int textX = x + (TILE_SIZE - charWidth) / 2;
        int textY = y + (TILE_SIZE + charHeight) / 2;
        g.drawString(" ", textX, textY);

        g.setColor(Color.WHITE);
        g.drawString(playerEmoji, textX, textY);
    }

    private void drawStatusBox(Graphics g, FontMetrics fm, int charWidth) {
        int boxSize = 64;
        int x = 0;
        int y = ROWS * TILE_SIZE; // bottom of the maze

        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, boxSize, boxSize);

        boolean nearEnemy = isNearEnemy(2.0);
        String face = player.getCurrentFrame(animationFrame, nearEnemy);

        int charHeight = fm.getAscent();
        int textX = x + (boxSize - charWidth) / 2;
        int textY = y + (boxSize + charHeight) / 2 - 8;

        g.setColor(Color.BLACK);
        g.drawString(" ", textX, textY);

        g.setColor(Color.WHITE);
        g.drawString(face, textX, textY);
    }

    private void drawDoomHUD(Graphics g, FontMetrics fm, int charWidth) {
        int hudY = ROWS * TILE_SIZE + 10;
        boolean nearEnemy = isNearEnemy(2.0);

        // pick doom face
        String[] doomFrames = player.pickDoomFace(nearEnemy);
        String doomFace = doomFrames[animationFrame];

        // show key if we have it
        String keyIcon = player.hasKey() ? "🔑" : " ";

        // background
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, ROWS*TILE_SIZE, COLS*TILE_SIZE, HUD_HEIGHT);

        int centerX = (COLS*TILE_SIZE)/2;
        int faceWidth = fm.stringWidth(doomFace);
        int faceHeight = fm.getAscent();
        int fx = centerX - faceWidth/2;
        int fy = hudY + faceHeight;

        int keyW = fm.stringWidth(keyIcon);
        int kx = fx + faceWidth + 20;
        int ky = fy;

        g.setColor(Color.WHITE);
        g.drawString(doomFace, fx, fy);
        g.drawString(keyIcon, kx, ky);
    }

    private boolean isNearEnemy(double range) {
        for (Enemy e : enemies) {
            if (distance(player.getRow(), player.getCol(), e.row, e.col) < range) {
                return true;
            }
        }
        return false;
    }

    private double distance(int r1, int c1, int r2, int c2) {
        double dr = r1 - r2;
        double dc = c1 - c2;
        return Math.sqrt(dr*dr + dc*dc);
    }

    // Movement / KeyListener

    @Override
    public void keyPressed(KeyEvent e) {
        if (player.hasWon()) return;

        int code = e.getKeyCode();
        int newRow = player.getRow();
        int newCol = player.getCol();

        switch(code) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W: newRow--; break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S: newRow++; break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A: newCol--; break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D: newCol++; break;
            default: return;
        }

        if (!validMove(newRow, newCol)) return;

        player.setRow(newRow);
        player.setCol(newCol);

        int tile = mazeData[newRow][newCol];
        if (tile == 2) {
            // pick up key
            player.setHasKey(true);
            player.triggerKeyCelebration(); // "funny" frames
            mazeData[newRow][newCol] = 0;
        } else if (tile == 3) {
            if (player.hasKey()) {
                player.setWon(true);
            }
        } else if (tile == 4) {
            player.decrementHealth();
            if (player.getHealth() <= 0) {
                resetPlayer();
            }
        }

        updateEnemies();
        repaint();
    }

    private boolean validMove(int r, int c) {
        if (r<0 || r>=ROWS || c<0 || c>=COLS) return false;
        int tile = mazeData[r][c];
        if (tile==1) return false; // wall
        if (tile==5 && !player.hasKey()) return false; // locked door
        if (tile==5 && player.hasKey()) {
            mazeData[r][c] = 0; // unlock
        }
        return true;
    }

    private void updateEnemies() {
        for (Enemy e : enemies) {
            moveEnemy(e);
        }
        // check collisions
        for (Enemy e : enemies) {
            if (!player.hasWon() && e.row == player.getRow() && e.col == player.getCol()) {
                player.decrementHealth();
                if (player.getHealth() <= 0) {
                    resetPlayer();
                }
            }
        }
    }

    private void moveEnemy(Enemy e) {
        int dRow = player.getRow() - e.row;
        int dCol = player.getCol() - e.col;

        if (Math.abs(dRow) > Math.abs(dCol)) {
            if (dRow > 0) tryMove(e, e.row+1, e.col);
            else if (dRow < 0) tryMove(e, e.row-1, e.col);
            else {
                if (dCol>0) tryMove(e, e.row, e.col+1);
                else if (dCol<0) tryMove(e, e.row, e.col-1);
            }
        } else {
            if (dCol>0) tryMove(e, e.row, e.col+1);
            else if (dCol<0) tryMove(e, e.row, e.col-1);
            else {
                if (dRow>0) tryMove(e, e.row+1, e.col);
                else if (dRow<0) tryMove(e, e.row-1, e.col);
            }
        }
    }

    private void tryMove(Enemy e, int nr, int nc) {
        if (nr<0||nr>=ROWS||nc<0||nc>=COLS) return;
        int tile = mazeData[nr][nc];
        if (tile==1||tile==5) return; // blocked
        e.row = nr;
        e.col = nc;
    }

    private void resetPlayer() {
        // Fix: use our defined startRow & startCol
        player.setRow(startRow);
        player.setCol(startCol);
        player.setHealth(3);
        // keep key & door states
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
