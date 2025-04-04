import java.util.Iterator;
import org.jogamp.java3d.Behavior;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.TransparencyAttributes;
import org.jogamp.java3d.WakeupCriterion;
import org.jogamp.java3d.WakeupOnCollisionEntry;
import org.jogamp.java3d.WakeupOnCollisionExit;
import org.jogamp.java3d.WakeupOr;

public class CollisionPreventionBehavior extends Behavior {
	private WakeupCriterion[] wakeupArray;
    private WakeupOr wakeupCondition;
    private boolean isColliding = false;
    private Shape3D playerShape;
    private TransformGroup playerTG;
    private Transform3D lastSafeTransform = new Transform3D();

    public CollisionPreventionBehavior(Shape3D player, TransformGroup tg) {
        this.playerShape = player;
        this.playerTG = tg;
        this.playerTG.getTransform(lastSafeTransform); // Store initial safe position
    }

    public void initialize() {
        wakeupArray = new WakeupCriterion[]{
            new WakeupOnCollisionEntry(playerShape, WakeupOnCollisionEntry.USE_GEOMETRY),
            new WakeupOnCollisionExit(playerShape, WakeupOnCollisionExit.USE_GEOMETRY)
        };
        wakeupCondition = new WakeupOr(wakeupArray);
        wakeupOn(wakeupCondition);
    }

    public void processStimulus(Iterator<WakeupCriterion> criteria) {
    	 TransparencyAttributes transparencyAttributes = new TransparencyAttributes();
         transparencyAttributes.setTransparencyMode(TransparencyAttributes.FASTEST);
        while (criteria.hasNext()) {
            WakeupCriterion criterion = criteria.next();

            if (criterion instanceof WakeupOnCollisionEntry) {
                isColliding = true;
                System.out.println("Collision detected! Reverting movement.");
                playerTG.setTransform(lastSafeTransform); // Revert position
            } else if (criterion instanceof WakeupOnCollisionExit) {
                isColliding = false;
            }
        }

        // Save the last safe position if not colliding
        if (!isColliding) {
            playerTG.getTransform(lastSafeTransform);
        }

        wakeupOn(wakeupCondition);
    }

    public boolean isColliding() {
        return isColliding;
    }
}

