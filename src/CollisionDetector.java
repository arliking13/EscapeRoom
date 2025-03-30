import org.jogamp.java3d.*;
import org.jogamp.vecmath.*;

public class CollisionDetector {
    private static final float PLAYER_RADIUS = 0.3f;
    private static final float PLAYER_HEIGHT = 1.8f;
    private BranchGroup scene;
    private BoundingSphere playerBounds;
    
    public CollisionDetector(BranchGroup scene) {
        this.scene = scene;
        this.playerBounds = new BoundingSphere(new Point3d(), PLAYER_RADIUS);
    }
    
    public boolean checkCollision(Vector3f newPosition) {
        // Update player bounds to new position
        playerBounds.setCenter(new Point3d(newPosition.x, newPosition.y, newPosition.z));
        
        // Check against all collision objects in the scene
        return checkNodeCollision(scene, playerBounds);
    }
    
    private boolean checkNodeCollision(Node node, Bounds bounds) {
        if (node instanceof Group) {
            Group group = (Group) node;
            for (int i = 0; i < group.numChildren(); i++) {
                Node child = group.getChild(i);
                if (checkNodeCollision(child, bounds)) {
                    return true;
                }
            }
        } else if (node instanceof Shape3D) {
            Shape3D shape = (Shape3D) node;
            Bounds shapeBounds = shape.getBounds();
            
            // Skip if shape has no bounds or is the player itself
            if (shapeBounds == null || shape.getUserData() != null && shape.getUserData().equals("player")) {
                return false;
            }
            
            if (shapeBounds.intersect(bounds)) {
                return true;
            }
        }
        return false;
    }
    
    public Vector3f adjustPosition(Vector3f desiredPosition, Vector3f currentPosition) {
        // Simple collision response - slide along obstacles
        Vector3f adjustedPosition = new Vector3f(desiredPosition);
        
        // Check X axis movement
        Vector3f testPos = new Vector3f(adjustedPosition.x, currentPosition.y, currentPosition.z);
        playerBounds.setCenter(new Point3d(testPos.x, testPos.y, testPos.z));
        if (checkNodeCollision(scene, playerBounds)) {
            adjustedPosition.x = currentPosition.x;
        }
        
        // Check Z axis movement
        testPos.set(currentPosition.x, currentPosition.y, adjustedPosition.z);
        playerBounds.setCenter(new Point3d(testPos.x, testPos.y, testPos.z));
        if (checkNodeCollision(scene, playerBounds)) {
            adjustedPosition.z = currentPosition.z;
        }
        
        // Check Y axis movement (for jumping/falling)
        testPos.set(adjustedPosition.x, adjustedPosition.y, adjustedPosition.z);
        playerBounds.setCenter(new Point3d(testPos.x, testPos.y, testPos.z));
        if (checkNodeCollision(scene, playerBounds)) {
            adjustedPosition.y = currentPosition.y;
        }
        
        return adjustedPosition;
    }
}