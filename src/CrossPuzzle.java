// CrossPuzzle.java
import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.picking.PickCanvas;
import org.jogamp.java3d.utils.picking.PickResult;
import org.jogamp.java3d.utils.picking.PickTool;

import org.jogamp.vecmath.*;
import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class CrossPuzzle {
    private TransformGroup crossMiddle;
    private int rotationCount = 0;
    private ArrayList<TransformGroup> connectedCrosses;
    private boolean debug = false; // Set to false to disable logs
    private ArrayList<Integer> connectedRotationCounts = new ArrayList<>();
    private boolean solved = false;
    private boolean alreadyNotified = false;


    


    
    public CrossPuzzle(TransformGroup crossMiddle) {
        this.crossMiddle = crossMiddle;
        this.connectedCrosses = new ArrayList<TransformGroup>();
        setupBehavior();
        
        // Set the middle cross’s rotation counter to 3,
        // so that effective state = (3 mod 4) which corresponds to -90°.
        rotationCount = 3;
    }

    
    public void addConnectedCross(TransformGroup cross) {
        connectedCrosses.add(cross);
        connectedRotationCounts.add(0); // Initial rotation count
    }
    

    private void setupBehavior() {
        Behavior clickBehavior = new Behavior() {
            private WakeupOnAWTEvent wakeCondition = new WakeupOnAWTEvent(MouseEvent.MOUSE_CLICKED);

            @Override
            public void initialize() {
                wakeupOn(wakeCondition);
            }

            @Override
            public void processStimulus(Iterator<WakeupCriterion> criteria) {
                while (criteria.hasNext()) {
                    WakeupCriterion criterion = criteria.next();
                    if (criterion instanceof WakeupOnAWTEvent) {
                        AWTEvent[] events = ((WakeupOnAWTEvent)criterion).getAWTEvent();
                        for (AWTEvent event : events) {
                            if (event instanceof MouseEvent me) {
                                if (me.getButton() == MouseEvent.BUTTON1) {
                                    // Only trigger if the source is inside the actual cross object
                                    if (me.getSource() instanceof Canvas3D canvas) {
                                        PickCanvas picker = new PickCanvas(canvas, crossMiddle.getLocale());
                                        picker.setMode(PickTool.GEOMETRY);
                                        picker.setTolerance(4.0f);
                                        picker.setShapeLocation(me);

                                        PickResult result = picker.pickClosest();
                                        if (result != null) {
                                            Node node = result.getObject();
                                            Node parent = node != null ? node.getParent() : null;

                                            while (parent != null) {
                                                if (parent == crossMiddle) {
                                                    rotateCross(); // ✅ Only rotate if actual cross is clicked
                                                    break;
                                                }
                                                parent = parent.getParent();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                wakeupOn(wakeCondition);
            }
        };

        BoundingSphere bounds = new BoundingSphere(new Point3d(0, 0, 0), 100.0);
        clickBehavior.setSchedulingBounds(bounds);
        crossMiddle.addChild(clickBehavior);
    }

    public void applyStartingRotation() {
        Transform3D rotation = new Transform3D();
        rotation.rotX(Math.toRadians(90));

        Transform3D currentTransform = new Transform3D();
        crossMiddle.getTransform(currentTransform);
        currentTransform.mul(rotation);
        crossMiddle.setTransform(currentTransform);

        rotationCount = 1; // 👈 set logic to match visual
    }
    
    


    private void checkIfPuzzleSolved() {
        if (rotationCount % 4 != 0) return;

        for (int count : connectedRotationCounts) {
            if (count % 4 != 0) return;
        }

        if (!solved) {
            System.out.println("✅ Puzzle solved! All crosses are upright.");
            solved = true;

            SoundEffects.load("bell_sound", false);
            SoundEffects.play("bell_sound");

            if (!alreadyNotified) {
                alreadyNotified = true;
                System.out.println("📢 Notifying Main that this puzzle is solved...");
                Main.notifyPuzzleSolved(crossMiddle);
            }
        }
    }




    
    private void rotateCross() {
        if (solved) return; // Do not rotate if puzzle is already solved

        // Create a 90° rotation transform around the X-axis.
        Transform3D rotation = new Transform3D();
        rotation.rotX(Math.toRadians(90));

        // Get the current transform, apply the new rotation, and update the middle cross.
        Transform3D currentTransform = new Transform3D();
        crossMiddle.getTransform(currentTransform);
        currentTransform.mul(rotation);
        crossMiddle.setTransform(currentTransform);

        // Increment the rotation counter.
        rotationCount++;

        // Play the cross rotation sound.
        SoundEffects.load("cross_sound", false); // Load without looping.
        SoundEffects.play("cross_sound");

        if (debug) {
            System.out.println("[DEBUG] Middle cross rotated. Total: " + rotationCount);
        }

        // If the middle cross has completed a full cycle (relative to its offset), rotate the connected crosses.
        if (rotationCount % 4 == 0) {
            if (debug) System.out.println("[DEBUG] 4 rotations. Rotating connected crosses...");

            for (int i = 0; i < connectedCrosses.size(); i++) {
                TransformGroup cross = connectedCrosses.get(i);
                cross.getTransform(currentTransform);
                currentTransform.mul(rotation);
                cross.setTransform(currentTransform);

                // Update the rotation counter for the connected cross.
                int newCount = connectedRotationCounts.get(i) + 1;
                connectedRotationCounts.set(i, newCount);

                if (debug) System.out.println("    - Rotated connected cross. Count: " + newCount);
            }

            // Check if the puzzle is solved.
            checkIfPuzzleSolved();
            Main.notifyPuzzleSolved(crossMiddle); // This is required!

        }
    }


}