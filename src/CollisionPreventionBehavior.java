import java.util.Iterator;

import org.jogamp.java3d.*;
import org.jogamp.java3d.BoundingSphere;

public class CollisionPreventionBehavior extends Behavior {
    private WakeupCriterion[] wakeupArray;
    private WakeupOr wakeupCondition;
    private boolean isColliding = false;
    private final TransformGroup playerTG;
    private final Transform3D lastSafeTransform = new Transform3D();
    private final Shape3D playerShape;
    private boolean debug = false;

    public CollisionPreventionBehavior(Shape3D player, TransformGroup tg) {
        if (player == null || tg == null) {
            throw new IllegalArgumentException("Player shape and TransformGroup cannot be null");
        }
        this.playerShape = player;
        this.playerTG = tg;
        this.playerTG.getTransform(lastSafeTransform); // Store initial safe position
    }

    // Call this method manually after construction to set up the wakeup criteria
    public void setupCollisionDetection() {
        wakeupArray = new WakeupCriterion[]{
            new WakeupOnCollisionEntry(playerShape, WakeupOnCollisionEntry.USE_GEOMETRY),
            new WakeupOnCollisionExit(playerShape, WakeupOnCollisionExit.USE_GEOMETRY)
        };
        wakeupCondition = new WakeupOr(wakeupArray);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public void initialize() {
        if (wakeupCondition == null) {
            throw new IllegalStateException("Call setupCollisionDetection() before initializing the behavior.");
        }
        wakeupOn(wakeupCondition);
    }

    @Override
    public void processStimulus(Iterator<WakeupCriterion> criteria) {
        while (criteria.hasNext()) {
            WakeupCriterion criterion = criteria.next();

            if (criterion instanceof WakeupOnCollisionEntry) {
                isColliding = true;
                if (debug) {
                    System.out.println("Collision detected! Reverting movement.");
                }
                playerTG.setTransform(lastSafeTransform); // Revert position
                onCollisionEnter();
            } else if (criterion instanceof WakeupOnCollisionExit) {
                isColliding = false;
                onCollisionExit();
            }
        }

        // Save the last safe position if not colliding
        if (!isColliding) {
            playerTG.getTransform(lastSafeTransform);
        }

        wakeupOn(wakeupCondition);
    }

    protected void onCollisionEnter() {
        if (debug) {
            System.out.println("Collision Enter Triggered");
        }
    }

    protected void onCollisionExit() {
        if (debug) {
            System.out.println("Collision Exit Triggered");
        }
    }

    public boolean isColliding() {
        return isColliding;
    }

    @Override
    public String toString() {
        return "CollisionPreventionBehavior for: " + playerShape.getName();
    }
}
