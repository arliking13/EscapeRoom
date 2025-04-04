import java.util.Iterator;
import org.jogamp.java3d.Behavior;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.WakeupCriterion;
import org.jogamp.java3d.WakeupOnCollisionEntry;
import org.jogamp.java3d.WakeupOnCollisionExit;
import org.jogamp.java3d.WakeupOr;

public class CollisionPreventionBehavior extends Behavior {
    private final WakeupCriterion[] wakeupArray;
    private final WakeupOr wakeupCondition;
    private boolean isColliding = false;
    private final Shape3D playerShape;
    private boolean debug = false;

    public CollisionPreventionBehavior(Shape3D player) {
        if (player == null) {
            throw new IllegalArgumentException("Player shape cannot be null");
        }
        this.playerShape = player;
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
        wakeupOn(wakeupCondition);
    }

    @Override
    public void processStimulus(Iterator<WakeupCriterion> criteria) {
        while (criteria.hasNext()) {
            WakeupCriterion criterion = criteria.next();

            if (criterion instanceof WakeupOnCollisionEntry) {
                isColliding = true;
                onCollisionEnter();
            } else if (criterion instanceof WakeupOnCollisionExit) {
                isColliding = false;
                onCollisionExit();
            }
        }
        wakeupOn(wakeupCondition);
    }

    protected void onCollisionEnter() {
        if (debug) System.out.println("Collision detected!");
    }

    protected void onCollisionExit() {
        if (debug) System.out.println("No longer colliding.");
    }

    public boolean isColliding() {
        return isColliding;
    }

    @Override
    public String toString() {
        return "CollisionPreventionBehavior for: " + playerShape.getName();
    }
}


