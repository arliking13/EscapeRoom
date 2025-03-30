package maze2d;

public class Player {
    private int row, col;
    private int health = 3;
    private boolean hasKey = false;
    private boolean hasWon = false;

    // short "celebration" time after picking the key
    private int keyCelebrationTimer = 0; // frames

    public Player(int startRow, int startCol) {
        this.row = startRow;
        this.col = startCol;
    }

    // Position
    public int getRow() { return row; }
    public int getCol() { return col; }
    public void setRow(int r) { row = r; }
    public void setCol(int c) { col = c; }

    // Health
    public int getHealth() { return health; }
    public void setHealth(int h) { health = h; }
    public void decrementHealth() { health--; }

    // Key
    public boolean hasKey() { return hasKey; }
    public void setHasKey(boolean k) { hasKey = k; }

    // Celebration
    public void triggerKeyCelebration() {
        // let's say we celebrate for 6 ticks
        keyCelebrationTimer = 6;
    }

    // Each animation tick, reduce celebration timer
    public void updateAnimation() {
        if (keyCelebrationTimer > 0) {
            keyCelebrationTimer--;
        }
    }

    // Win
    public boolean hasWon() { return hasWon; }
    public void setWon(boolean w) { hasWon = w; }

    /**
     * Returns the emoji the player should show on the grid, based on:
     *  1) If we are celebrating (keyCelebrationTimer>0), show "funny" frames
     *  2) If health <=1 => low health frames
     *  3) If near enemy => near-enemy frames
     *  4) If hasKey => has-key frames
     *  5) else => default
     */
    public String getCurrentFrame(int animationFrame, boolean nearEnemy) {
        // 1) if celebrating
        if (keyCelebrationTimer > 0) {
            // special 2-frame: "🤩" / "🎉"
            String[] celebrateFrames = {"\uD83E\uDD29","\uD83C\uDF89"}; // 🤩, 🎉
            return celebrateFrames[animationFrame];
        }
        // 2) low health
        if (health <= 1) {
            return Emotes.playerFramesLowHealth[animationFrame];
        }
        // 3) near enemy
        if (nearEnemy) {
            return Emotes.playerFramesNearEnemy[animationFrame];
        }
        // 4) has key
        if (hasKey) {
            return Emotes.playerFramesHasKey[animationFrame];
        }
        // 5) default
        return Emotes.playerFramesDefault[animationFrame];
    }

    /**
     * The "doom face" for the HUD
     */
    public String[] pickDoomFace(boolean nearEnemy) {
        // If we are still celebrating, let's be "happy"
        if (keyCelebrationTimer > 0) {
            return new String[]{"\uD83D\uDE01","\uD83E\uDD17"}; // 😁, 🤗
        }
        // else normal logic
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
