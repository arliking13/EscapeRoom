package maze2d;

public class Player {
    private int row, col;
    private int health = 3;
    private boolean hasKey = false;
    private boolean hasWon = false;
    private int keyCelebrationTimer = 0;

    public Player(int startRow, int startCol) {
        this.row = startRow;
        this.col = startCol;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public void setRow(int r) { row = r; }
    public void setCol(int c) { col = c; }

    public int getHealth() { return health; }
    public void setHealth(int h) { health = h; }
    public void decrementHealth() { health--; }

    public boolean hasKey() { return hasKey; }
    public void setHasKey(boolean k) { hasKey = k; }

    public void triggerKeyCelebration() {
        keyCelebrationTimer = 6;
    }

    public void updateAnimation() {
        if (keyCelebrationTimer > 0) {
            keyCelebrationTimer--;
        }
    }

    public boolean hasWon() { return hasWon; }
    public void setWon(boolean w) { hasWon = w; }

    public String getCurrentFrame(int animationFrame, boolean nearEnemy) {
        if (keyCelebrationTimer > 0) {
            String[] celebrateFrames = {"\uD83E\uDD29","\uD83C\uDF89"};
            return celebrateFrames[animationFrame];
        }
        if (health <= 1) {
            return Emotes.playerFramesLowHealth[animationFrame];
        }
        if (nearEnemy) {
            return Emotes.playerFramesNearEnemy[animationFrame];
        }
        if (hasKey) {
            return Emotes.playerFramesHasKey[animationFrame];
        }
        return Emotes.playerFramesDefault[animationFrame];
    }

    public String[] pickDoomFace(boolean nearEnemy) {
        if (keyCelebrationTimer > 0) {
            return new String[]{"\uD83D\uDE01","\uD83E\uDD17"};
        }
        if (health <= 1) {
            return Emotes.doomFacesLowHealth;
        }
        if (nearEnemy) {
            return Emotes.doomFacesNearEnemy;
        }
        if (hasKey) {
            return Emotes.doomFacesHasKey;
        }
        return Emotes.doomFacesDefault;
    }
}