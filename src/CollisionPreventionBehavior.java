import java.util.Iterator;
import org.jogamp.java3d.Behavior;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.WakeupCriterion;
import org.jogamp.java3d.WakeupOnCollisionEntry;
import org.jogamp.java3d.WakeupOnCollisionExit;
import org.jogamp.java3d.WakeupOr;

public class CollisionPreventionBehavior extends Behavior {
	private WakeupCriterion[] wakeupArray;
    private WakeupOr wakeupCondition;
    private boolean isColliding = false;
    private Shape3D playerShape;

    public CollisionPreventionBehavior(Shape3D player) {
        this.playerShape = player;
    }

    public void initialize() {
        wakeupArray = new WakeupCriterion[]{
            new WakeupOnCollisionEntry(playerShape, WakeupOnCollisionEntry.USE_GEOMETRY),
            new WakeupOnCollisionExit(playerShape, WakeupOnCollisionExit.USE_GEOMETRY)
        };
        wakeupCondition = new WakeupOr(wakeupArray);
        wakeupOn(wakeupCondition);
    }

    public void processStimulus(Iterator criteria) {
        while (criteria.hasNext()) {
            WakeupCriterion criterion = (WakeupCriterion) criteria.next();

            if (criterion instanceof WakeupOnCollisionEntry) {
                isColliding = true;
                System.out.println("Collision detected!");
            } else if (criterion instanceof WakeupOnCollisionExit) {
                isColliding = false;
                System.out.println("No longer colliding.");
            }
        }
        wakeupOn(wakeupCondition);
    }

    public boolean isColliding() {
        return isColliding;
    }
}

