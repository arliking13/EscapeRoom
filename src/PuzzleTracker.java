import org.jogamp.java3d.TransformGroup;
import java.util.HashSet;
import java.util.Set;

/**
 * Tracks progress of different puzzles and plays a sound once all are completed.
 */
public class PuzzleTracker {

    private static final Set<Object> solvedPuzzles = new HashSet<>();
    private static boolean doorActivated = false;
    private static boolean escapeDoorCanBeOpened = false;

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
     * If both puzzles are solved, play activation then opening sound automatically.
     */
    private static void checkAndPlayDoorActivated() {
        if (solvedPuzzles.size() == 2 && !doorActivated) {
            doorActivated = true;
            System.out.println("🚪 Both puzzles solved. Playing activation sound...");
            SoundEffects.load("door_activated", false);
            SoundEffects.play("door_activated");

            new Thread(() -> {
                try {
                    Thread.sleep(2500); // Wait for door_activated.wav to finish
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                escapeDoorCanBeOpened = true;
                System.out.println("✅ Door is now openable.");
            }).start();
        }
    }


    public static boolean canOpenEscapeDoor() {
        return escapeDoorCanBeOpened;
    }

    public static boolean allPuzzlesSolved() {
        return doorActivated;
    }

    public static void reset() {
        solvedPuzzles.clear();
        doorActivated = false;
        escapeDoorCanBeOpened = false;
    }
}
