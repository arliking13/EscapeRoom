import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;
import org.jogamp.vecmath.Vector3f;
import java.awt.event.*;

public class CameraControl implements KeyListener{
	private TransformGroup viewTransformGroup;
    private Transform3D transform = new Transform3D();
    private Vector3f position = new Vector3f(0.0f, 0.0f, 5.0f); // Initial position
    private float angleY = 0.0f;
    private float angleX = 0.0f;

    public CameraControl(TransformGroup tg) {
        this.viewTransformGroup = tg;
        Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        add(canvas);
        canvas.addKeyListener((KeyListener) this);

        // Create a SimpleUniverse
        SimpleUniverse universe = new SimpleUniverse(canvas);

        // Get the ViewingPlatform and its TransformGroup
        viewTransformGroup = universe.getViewingPlatform().getViewPlatformTransform();
        transform.lookAt(new Point3d(0.0, 0.0, 10.0), new Point3d(0.0, 0.0, 0.0), new Vector3d(0.0, 1.0, 0.0)); 
        transform.invert();

        // Setup the scene
        BranchGroup scene = createSceneGraph();
        universe.addBranchGraph(scene);

        // Enable the canvas to receive focus for key events
        canvas.requestFocus();
        transform.setTranslation(position);
        viewTransformGroup.setTransform(transform);
    }

    private void add(Canvas3D canvas) {
		// TODO Auto-generated method stub
		
	}

	private BranchGroup createSceneGraph() {
		// TODO Auto-generated method stub
		return null;
	}

	public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                angleY -= 0.1f; // Rotate left
                break;
            case KeyEvent.VK_RIGHT:
                angleY += 0.1f; // Rotate right
                break;
            case KeyEvent.VK_UP:
                angleX -= 0.1f; // Look up
                break;
            case KeyEvent.VK_DOWN:
                angleX += 0.1f; // Look down
                break;
        }

        Transform3D rotX = new Transform3D();
        Transform3D rotY = new Transform3D();
        rotX.rotX(angleX);
        rotY.rotY(angleY);
        
        transform.setIdentity();
        transform.mul(rotY);
        transform.mul(rotX);
        transform.setTranslation(position);

        viewTransformGroup.setTransform(transform);
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

}
