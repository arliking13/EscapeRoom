import org.jogamp.java3d.*;
import org.jogamp.vecmath.*;
import java.awt.event.*;

public class CameraControl implements KeyListener, MouseMotionListener{
	private TransformGroup viewTransformGroup;
    private Transform3D transform = new Transform3D();
    private Vector3f position = new Vector3f(0.0f, -5f, 5.0f);
    private float angleY = 0.0f;   // Yaw (left/right rotation)
    private float angleX = 0.0f;   // Pitch (up/down rotation)
    private float moveSpeed = 0.2f;
    private float rotationSpeed = 0.05f;

    public CameraControl(TransformGroup tg) {
        this.viewTransformGroup = tg;
        viewTransformGroup.getTransform(transform);
        updateTransform();
    }

    private void add(Canvas3D canvas) {
		// TODO Auto-generated method stub
		
	}

	private BranchGroup createSceneGraph() {
		BranchGroup root = new BranchGroup();
        TransformGroup objTransform = new TransformGroup();
        objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        root.addChild(objTransform);
        return root;
	}

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        // Movement controls
        Vector3f forward = new Vector3f();
        Vector3f side = new Vector3f();
        transform.get(forward);
        forward.z = -forward.z; // Invert Z for forward direction
        side.set(forward.z, 0, -forward.x); // Correct perpendicular vector for strafing
        
        switch (keyCode) {
            case KeyEvent.VK_W: // Move forward
                position.x += forward.x * moveSpeed;
                position.y += forward.y * moveSpeed;
                position.z += forward.z * moveSpeed;
                break;
            case KeyEvent.VK_S: // Move backward
                position.x -= forward.x * moveSpeed;
                position.y -= forward.y * moveSpeed;
                position.z -= forward.z * moveSpeed;
                break;
            case KeyEvent.VK_D: // Strafe left (corrected)
                position.x -= side.x * moveSpeed;
                position.z -= side.z * moveSpeed;
                break;
            case KeyEvent.VK_A: // Strafe right (corrected)
                position.x += side.x * moveSpeed;
                position.z += side.z * moveSpeed;
                break;
            case KeyEvent.VK_SPACE: // Move up
                position.y += moveSpeed;
                break;
            case KeyEvent.VK_SHIFT: // Move down
                position.y -= moveSpeed;
                break;
            case KeyEvent.VK_LEFT: // Look left
                angleY += rotationSpeed;
                break;
            case KeyEvent.VK_RIGHT: // Look right
                angleY -= rotationSpeed;
                break;
            case KeyEvent.VK_UP: // Look up
                angleX += rotationSpeed;
                break;
            case KeyEvent.VK_DOWN: // Look down
                angleX -= rotationSpeed;
                break;
        }
        
        updateTransform();
    }

    private void updateTransform() {
    	System.out.println("Camera position: " + position);
        // Limit pitch to avoid gimbal lock
        angleX = Math.max(-(float)Math.PI/2, Math.min((float)Math.PI/2, angleX));
        
        Transform3D rotationX = new Transform3D();
        Transform3D rotationY = new Transform3D();
        rotationX.rotX(angleX);
        rotationY.rotY(angleY);
        
        transform.setIdentity();
        transform.mul(rotationY);
        transform.mul(rotationX);
        transform.setTranslation(position);
        
        viewTransformGroup.setTransform(transform);
    }
	
	public void mouseDragged(MouseEvent e) {
        // Update angles based on mouse movement
        angleX += e.getX() * 0.01;
        angleY += e.getY() * 0.01;

        // Apply rotations to the view transform
        Transform3D rotX = new Transform3D();
        Transform3D rotY = new Transform3D();
        rotX.rotX(angleY);
        rotY.rotY(angleX);
        transform.mul(rotX);
        transform.mul(rotY);

        // Update the view transform group
        viewTransformGroup.setTransform(transform);
    }

    public void mouseMoved(MouseEvent e) { }
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}