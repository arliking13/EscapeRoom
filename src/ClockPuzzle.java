import org.jogamp.java3d.AmbientLight;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.Node;
import org.jogamp.java3d.PickInfo;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.loaders.Scene;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.pickfast.PickCanvas;
import org.jogamp.java3d.utils.picking.PickResult;
import org.jogamp.java3d.utils.picking.PickTool;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.AxisAngle4d;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;
import org.jogamp.vecmath.Vector3f;

import java.awt.event.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFrame;

public class ClockPuzzle extends JFrame implements MouseListener, KeyListener {
	private static final long serialVersionUID = 1L;
    private SimpleUniverse universe;
    private BranchGroup root;
    private Canvas3D canvas;
    private PickCanvas pickCanvas;
    private ArrayList<Integer> inputSequence = new ArrayList<>();
    private static final int[] CORRECT_PASSCODE = {1, 0, 2}; // Correct Code
    private TransformGroup keypadTG;

    public ClockPuzzle() {
        // Set up Java3D Canvas
        setTitle("Clock Puzzle");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        add(canvas);

        universe = new SimpleUniverse(canvas);
        root = createScene();
        root.compile();
        
        // PickCanvas to detect clicks on 3D objects
        pickCanvas = new PickCanvas(canvas, root);
        pickCanvas.setMode(PickTool.BOUNDS);
        
        universe.getViewingPlatform().setNominalViewingTransform();
        universe.addBranchGraph(root);
    }
    
    public void createClockPuzzle() {
    	TransformGroup safeTG =  new CreateObjects().createObject("KeypadDoorLock", new AxisAngle4d(0, 1, 0, 0), new Vector3d(-0.7, -0.3, 0.1), 0.2);  
    	safeTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    	safeTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        root.addChild(safeTG);
    }

    private BranchGroup createScene() {
        BranchGroup scene = new BranchGroup();
        
        // Create 3x3 Keypad Layout
        keypadTG = new TransformGroup();
        keypadTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        keypadTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        keypadTG.setUserData("KeypadDoorLock");
        
        BranchGroup keypad = loadObj("KeypadDoorLock.obj");
        LoadObject.setObjectTexture("KeypadDoorLock", "KeypadDoorLockAlbedo.jpg");
        keypadTG.addChild(keypad);
        scene.addChild(keypadTG);
        
        AmbientLight light = new AmbientLight(true, new Color3f(1f, 1f, 1f));
        light.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 100));
        scene.addChild(light);
        
        return scene;
    }

    private BranchGroup loadObj(String filename) {
        BranchGroup group = new BranchGroup();
        try {
            ObjectFile loader = new ObjectFile(ObjectFile.RESIZE);
            Scene scene = loader.load(new FileReader(filename));
            group.addChild(scene.getSceneGroup());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return group;
    }

    private void checkPassword() {
        if (inputSequence.size() == CORRECT_PASSCODE.length) {
            if (inputSequence.equals(java.util.Arrays.asList(1, 0, 2))) {
            	SoundEffect
                unlock();
            } else {
                System.out.println("Wrong Code! Try Again.");
            }
            inputSequence.clear();
        }
    }
    
    public boolean unlock() {
    	
    }
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyChar() - '0';
        if (key >= 0 && key <= 9) {
            inputSequence.add(key);
            checkPassword();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    
    @Override
    public void mouseClicked(MouseEvent e) {
        pickCanvas.setShapeLocation(e.getX(), e.getY());
        PickInfo result = pickCanvas.pickClosest();
        if (result != null) {
        	Node pickedNode = result.getNode();
            if (pickedNode != null && "KeypadDoorLock".equals(pickedNode.getUserData())) {
                System.out.println("Keypad selected. Enter numbers using keyboard.");
            }
        }
    }

    public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
    public static void main(String[] args) {
        new ClockPuzzle().setVisible(true);
    }
}
