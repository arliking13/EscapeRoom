import org.jogamp.java3d.*;
import org.jogamp.vecmath.*;
import java.awt.event.*;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Point;

public class PlayerControls implements KeyListener, MouseMotionListener {
    private TransformGroup viewTransformGroup;
    private Transform3D transform = new Transform3D();
    private Vector3f position = new Vector3f(0.0f, 1.5f, 5.0f);
    private float angleY = 0.0f;   // Yaw (left/right rotation)
    private float angleX = 0.0f;   // Pitch (up/down rotation)
    private float moveSpeed = 0.2f;
    private float rotationSpeed = 0.05f;
    
    // Movement states
    private boolean forward = false;
    private boolean backward = false;
    private boolean left = false;
    private boolean right = false;
    private boolean up = false;
    private boolean down = false;
    
    private Robot robot;
    private Point lastMousePos;
    private boolean mouseCaptured = false;
    
    public PlayerControls(TransformGroup tg, Canvas3D canvas) {
        this.viewTransformGroup = tg;
        viewTransformGroup.getTransform(transform);
        updateTransform();
        
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        
        // Initialize mouse position
        lastMousePos = new Point(canvas.getWidth()/2, canvas.getHeight()/2);
        centerMouse(canvas);
    }
    
    private void centerMouse(Canvas3D canvas) {
        if (robot != null) {
            Point canvasLoc = canvas.getLocationOnScreen();
            int centerX = canvasLoc.x + canvas.getWidth()/2;
            int centerY = canvasLoc.y + canvas.getHeight()/2;
            robot.mouseMove(centerX, centerY);
            lastMousePos = new Point(centerX, centerY);
        }
    }
    
    public void update() {
        // Calculate movement based on current orientation
        Vector3f forwardVec = new Vector3f();
        Vector3f sideVec = new Vector3f();
        transform.get(forwardVec);
        forwardVec.z = -forwardVec.z; // Invert Z for forward direction
        sideVec.set(forwardVec.z, 0, -forwardVec.x); // Perpendicular vector for strafing
        
        // Apply movement based on key states
        if (forward) {
            position.x += forwardVec.x * moveSpeed;
            position.y += forwardVec.y * moveSpeed;
            position.z += forwardVec.z * moveSpeed;
        }
        if (backward) {
            position.x -= forwardVec.x * moveSpeed;
            position.y -= forwardVec.y * moveSpeed;
            position.z -= forwardVec.z * moveSpeed;
        }
        if (right) {
            position.x += sideVec.x * moveSpeed;
            position.z += sideVec.z * moveSpeed;
        }
        if (left) {
            position.x -= sideVec.x * moveSpeed;
            position.z -= sideVec.z * moveSpeed;
        }
        if (up) {
            position.y += moveSpeed;
        }
        if (down) {
            position.y -= moveSpeed;
        }
        
        updateTransform();
    }
    
    private void updateTransform() {
        // Limit pitch to avoid gimbal lock
        angleX = Math.max(-(float)Math.PI/3, Math.min((float)Math.PI/3, angleX));
        
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

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: forward = true; break;
            case KeyEvent.VK_S: backward = true; break;
            case KeyEvent.VK_A: left = true; break;
            case KeyEvent.VK_D: right = true; break;
            case KeyEvent.VK_SPACE: up = true; break;
            case KeyEvent.VK_SHIFT: down = true; break;
            case KeyEvent.VK_ESCAPE: 
                mouseCaptured = !mouseCaptured;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: forward = false; break;
            case KeyEvent.VK_S: backward = false; break;
            case KeyEvent.VK_A: left = false; break;
            case KeyEvent.VK_D: right = false; break;
            case KeyEvent.VK_SPACE: up = false; break;
            case KeyEvent.VK_SHIFT: down = false; break;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!mouseCaptured) return;
        
        Point currentPos = e.getPoint();
        int dx = currentPos.x - lastMousePos.x;
        int dy = currentPos.y - lastMousePos.y;
        
        // Update angles based on mouse movement
        angleY -= dx * rotationSpeed;
        angleX += dy * rotationSpeed;
        
        centerMouse((Canvas3D)e.getSource());
    }
    
    // Unused interface methods
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {}
}