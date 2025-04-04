import org.jogamp.java3d.TransformGroup;
import java.util.HashSet;
import java.util.Set;

/**
 * Tracks progress of different puzzles and plays a sound once all are completed.
 */
public class PuzzleTracker {

    // Store both cross puzzles (as TransformGroup) and maze (as "MAZE" string)
    private static final Set<Object> solvedPuzzles = new HashSet<>();
    private static boolean doorActivated = false;

    /**
     * Call this from CrossPuzzle when a cross is solved.
     */
    public static void reportCrossSolved(TransformGroup crossGroup) {
        if (crossGroup == null || solvedPuzzles.contains(crossGroup)) return;

        solvedPuzzles.add(crossGroup);
        System.out.println("🧩 Cross puzzle solved! Total solved: " + solvedPuzzles.size());

        checkAndPlayDoorActivated();
    }

    /**
     * Call this from MazeGameState when the maze is won.
     */
    public static void reportMazeSolved() {
        if (solvedPuzzles.contains("MAZE")) return;

        solvedPuzzles.add("MAZE");
        System.out.println("🧩 Maze puzzle solved! Total solved: " + solvedPuzzles.size());

        checkAndPlayDoorActivated();
    }

    /**
     * If both puzzles are solved, play the door activation sound once.
     */
    private static void checkAndPlayDoorActivated() {
        if (solvedPuzzles.size() == 2 && !doorActivated) {
            doorActivated = true;
            System.out.println("🚪 Both puzzles solved. Playing activation sound...");
            SoundEffects.load("door_activated", false);
            SoundEffects.play("door_activated");

            // Delay enabling door opening to simulate sound duration (e.g., 2.5s)
            new Thread(() -> {
                try {
                    Thread.sleep(2500); // wait for 2.5 seconds (match the actual sound length!)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                escapeDoorCanBeOpened = true;
                System.out.println("✅ Door is now openable.");
            }).start();
        }
    }


    /**
     * Optional: reset the tracker state (useful for restarting the game).
     */
    public static void reset() {
        solvedPuzzles.clear();
        doorActivated = false;
    }

    /**
     * Returns true if both puzzles are completed.
     */
    public static boolean allPuzzlesSolved() {
        return doorActivated;
    }
    private static boolean escapeDoorCanBeOpened = false;

    public static boolean canOpenEscapeDoor() {
        return escapeDoorCanBeOpened;
    }

}
