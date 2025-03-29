import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.loaders.Scene;
import javax.media.j3d.*;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.universe.*;
import org.jogamp.vecmath.*;
import java.io.IOException;

public class ShedRoom extends JPanel {
	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	
	private GameCanvas canvas = new GameCanvas();
	
    private BranchGroup lightBG = new BranchGroup();
    
	private SimpleUniverse universe = new SimpleUniverse(canvas);
	
	private Point3d camera = new Point3d(0, 0.5, 0); // define the point where the eye is
	private Point3d centerPoint = new Point3d(1.0, 0.5, 0.0); // define the point where the eye is looking
	private final BoundingSphere hundredBS = new BoundingSphere(new Point3d(), 1000.0);
	
	private TransformGroup viewTransform = universe.getViewingPlatform().getViewPlatformTransform();
	private Vector3d upDir = new Vector3d(0, 1, 0); // define camera's up direction
	private Transform3D transform = new Transform3D();
	
	public ShedRoom() throws IOException {
        Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        add("Center", canvas);
        SimpleUniverse universe = new SimpleUniverse(canvas);
        BranchGroup scene = createScene();
        scene.compile();
        universe.getViewingPlatform().setNominalViewingTransform();
        universe.addBranchGraph(scene);
		
		scene = createScene();
		scene.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		scene.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		scene.compile(); // optimize the BranchGroup
		universe.addBranchGraph(scene); // attach the scene to SimpleUniverse
		
		StartScreen startScreen = new StartScreen(this);
		
		setLayout(new BorderLayout());
		add("Center", startScreen);

		// Add the key and mouse controls
		universe.getCanvas().addKeyListener();
		universe.getCanvas().addMouseListener();
		universe.getCanvas().addMouseMotionListener();
		universe.getViewer().getView().setFieldOfView(1.5);
		SoundEffects.enableAudio(universe);
		updateViewer();

		frame.setSize(1920, 1080);
		frame.setTitle("The Shed");
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}
	
	public BranchGroup createScene() {
		BranchGroup scene = new BranchGroup();
		Transform3D scale = new Transform3D();
		scale.setScale(10);
		TransformGroup scaleTG = new TransformGroup(scale);
		scaleTG.addChild(CreateObjects.room());
		scene.addChild(scaleTG);
		scene.addChild(addLights(new Color3f(0.1f, 0.1f, 0.1f)));
		scene.addChild(SoundEffects.bkgdSound());

		return scene;
	}
	
	public Point3d getCamera() {
		return camera;
	}

	public void getCoords() {
		System.out.println("Camera: " + camera.x + ", " + camera.y + ", " +
				camera.z);
	}
	
	public static void main(String[] args) throws IOException {
		frame.getContentPane().add(new ShedRoom());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
