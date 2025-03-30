import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Vector3f;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class CrossRotationPuzzle extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
    private Cross3D cross1, cross2, cross3, cross4, cross5;
    private Timer timer;
    private int step = 0;
    
    public CrossRotationPuzzle() {
        setTitle("Rotating Cross Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        add("Center", canvas);
        
        BranchGroup scene = createSceneGraph();
        scene.compile();
        
        SimpleUniverse universe = new SimpleUniverse(canvas);
        universe.getViewingPlatform().setNominalViewingTransform();
        universe.addBranchGraph(scene);
        
        timer = new Timer(100, this);
        timer.start();
    }
    
    private BranchGroup createSceneGraph() {
        BranchGroup objRoot = new BranchGroup();
        TransformGroup rootTG = new TransformGroup();
        rootTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objRoot.addChild(rootTG);
        
        cross1 = new Cross3D("room3/Cross_left.obj");
        cross2 = new Cross3D("room3/Cross_middle.obj");
        cross3 = new Cross3D("room3/Cross_right.obj");
        cross4 = new Cross3D("room3/Cross_The_leftmost_cross.obj");
        cross5 = new Cross3D("room3/Cross_The_rightmost_cross.obj");
        
        Transform3D pos1 = new Transform3D();
        pos1.setTranslation(new Vector3f(-0.5f, 0, 0));
        TransformGroup tg1 = new TransformGroup(pos1);
        tg1.addChild(cross1.getTransformGroup());
        rootTG.addChild(tg1);

        Transform3D pos2 = new Transform3D();
        pos2.setTranslation(new Vector3f(0, 0, 0));
        TransformGroup tg2 = new TransformGroup(pos2);
        tg2.addChild(cross2.getTransformGroup());
        rootTG.addChild(tg2);

        Transform3D pos3 = new Transform3D();
        pos3.setTranslation(new Vector3f(0.5f, 0, 0));
        TransformGroup tg3 = new TransformGroup(pos3);
        tg3.addChild(cross3.getTransformGroup());
        rootTG.addChild(tg3);
        
        Transform3D pos4 = new Transform3D();
        pos4.setTranslation(new Vector3f(0.5f, 0, 0));
        TransformGroup tg4 = new TransformGroup(pos4);
        tg4.addChild(cross4.getTransformGroup());
        rootTG.addChild(tg4);
        
        Transform3D pos5 = new Transform3D();
        pos3.setTranslation(new Vector3f(0.5f, 0, 0));
        TransformGroup tg5 = new TransformGroup(pos5);
        tg5.addChild(cross5.getTransformGroup());
        rootTG.addChild(tg5);
        
        
        return objRoot;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
    	switch (step % 3) {
        case 0 -> cross1.rotate();
        case 1 -> cross2.rotate();
        case 2 -> cross3.rotate();
        case 3 -> cross4.rotate();
        case 4 -> cross5.rotate();
    	}
    	step++;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CrossRotationPuzzle game = new CrossRotationPuzzle();
            game.setVisible(true);
        });
    }
}

