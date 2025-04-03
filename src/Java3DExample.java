import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.behaviors.mouse.MouseRotate;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.geometry.Primitive;
import org.jogamp.java3d.utils.picking.PickCanvas;
import org.jogamp.java3d.utils.picking.PickResult;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Java3DExample extends JFrame {
    private static final long serialVersionUID = 1L;

    // Fields for the default (standalone) mode
    private Canvas3D canvas;
    private SimpleUniverse universe;
    private PickCanvas pickCanvas;
    private Clip clip;
    private Box box;
    private TransformGroup cubeTransformGroup;
    private MazeGameState gameState;

    // Maze-related fields
    private MazePanel mazePanel;
    private Texture2D dynamicTexture;
    private ImageComponent2D imageComponent;

    // Field to store the scene root when in integrated mode.
    private BranchGroup integratedRoot = null;

    // Default constructor: sets up the full GUI with JFrame, canvas, etc.
    public Java3DExample() {
        setTitle("Java3D Cube with Live Maze Texture");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Canvas setup
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        add("Center", canvas);
        canvas.setFocusable(true);
        canvas.requestFocusInWindow();
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (mazePanel != null) {
                    mazePanel.dispatchEvent(e);
                }
            }
        });

        // Toggle Button for 2D/3D view
        JButton toggleBtn = new JButton("Toggle 2D / 3D View");
        add("South", toggleBtn);
        toggleBtn.addActionListener(new ActionListener() {
            private boolean is2D = false;
            public void actionPerformed(ActionEvent e) {
                is2D = !is2D;
                toggleProjection(is2D);
            }
        });

        // Scene Setup
        universe = new SimpleUniverse(canvas);
        BranchGroup scene = createSceneGraph();
        universe.getViewingPlatform().setNominalViewingTransform();
        universe.addBranchGraph(scene);

        // Sound loading
        try {
            File soundFile = new File("sound/door-open_D_minor.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
        } catch (Exception e) {
            System.err.println("Error loading sound: " + e.getMessage());
            e.printStackTrace();
        }

        // Pick setup
        pickCanvas = new PickCanvas(canvas, scene);
        pickCanvas.setMode(PickCanvas.BOUNDS);

        // Click detection
        canvas.addMouseListener(new MouseAdapter() {
            private long lastClickTime = 0;
            public void mouseClicked(MouseEvent e) {
                long now = System.currentTimeMillis();
                if (now - lastClickTime < 300) { // Double-click threshold
                    pickCanvas.setShapeLocation(e);
                    PickResult result = pickCanvas.pickClosest();
                    if (result != null && result.getObject() instanceof Shape3D) {
                        Shape3D shape = (Shape3D) result.getObject();
                        if (shape == box.getShape(Box.FRONT)) {
                            System.out.println("✅ Double-clicked the screen!");
                            playSound();
                            canvas.requestFocusInWindow(); // Ensure key listener works
                        }
                    }
                }
                lastClickTime = now;
            }
        });
    }

    // Overloaded constructor for integrated mode:
    // This mode only builds the scene graph in memory (unattached) without creating a live universe.
    public Java3DExample(boolean integratedMode) {
        if (!integratedMode) {
            throw new IllegalArgumentException("For non-integrated mode, use the default constructor.");
        } else {
            // Integrated mode: build the scene graph in memory and store the root.
            BranchGroup scene = createSceneGraph();
            integratedRoot = scene;
            // Note: We do NOT create a SimpleUniverse or add the scene to a live universe.
        }
    }

    private void playSound() {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    private void toggleProjection(boolean is2D) {
        // Only applicable in default mode; in integrated mode universe is not created.
        if (universe != null) {
            View view = universe.getViewer().getView();
            if (is2D) {
                view.setProjectionPolicy(View.PARALLEL_PROJECTION);
            } else {
                view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
            }
        }
    }

    private BranchGroup createSceneGraph() {
        BranchGroup root = new BranchGroup();

        // 1. Create and store transform group for the cube
        TransformGroup tg = new TransformGroup();
        cubeTransformGroup = tg;
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        root.addChild(tg);

        // 2. Add mouse rotate behavior
        MouseRotate rotate = new MouseRotate();
        rotate.setTransformGroup(tg);
        rotate.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        root.addChild(rotate);

        // 3. Define bounds for lighting
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

        // 4. Lighting
        AmbientLight light = new AmbientLight(new Color3f(1.0f, 1.0f, 1.0f));
        light.setInfluencingBounds(bounds);
        root.addChild(light);

     // 5. Setup maze panel to image conversion
        gameState = new MazeGameState();
        mazePanel = new MazePanel(gameState);

        // Let the panel define its own preferred size properly
        Dimension pref = mazePanel.getPreferredSize();
        mazePanel.setSize(pref);
        mazePanel.setDoubleBuffered(true);
        mazePanel.doLayout(); // Ensure layout is applied

        BufferedImage image = new BufferedImage(pref.width, pref.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        mazePanel.paint(g2d);
        g2d.dispose();


        // 6. Texture setup
        imageComponent = new ImageComponent2D(ImageComponent.FORMAT_RGBA, image);
        imageComponent.setCapability(ImageComponent.ALLOW_IMAGE_WRITE);

        dynamicTexture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
        dynamicTexture.setCapability(Texture.ALLOW_IMAGE_WRITE);
        dynamicTexture.setImage(0, imageComponent);
        dynamicTexture.setEnable(true);

        Appearance mazeAppearance = new Appearance();
        mazeAppearance.setTexture(dynamicTexture);

        TextureAttributes texAttr = new TextureAttributes();
        Transform3D texTransform = new Transform3D();
        texTransform.setScale(1.0); // Fill full face
        texTransform.setTranslation(new Vector3f(0f, 0f, 0f));
        texAttr.setTextureTransform(texTransform);
        texAttr.setTextureMode(TextureAttributes.REPLACE);

        mazeAppearance.setTextureAttributes(texAttr);

        // 7. Default appearance for other faces
        Appearance defaultApp = new Appearance();
        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(0.2f, 0.6f, 1.0f);
        defaultApp.setColoringAttributes(ca);

        // 8. Create the cube
        box = new Box(0.3f, 0.25f, 0.01f, Primitive.GENERATE_TEXTURE_COORDS, defaultApp);

        // 9. Assign the textured face to the front of the cube
        box.getShape(Box.FRONT).setAppearance(mazeAppearance);
        box.setCapability(Box.ALLOW_PICKABLE_READ);
        box.setCapability(Box.ALLOW_PICKABLE_WRITE);
        box.setPickable(true);

        Shape3D front = box.getShape(Box.FRONT);
        front.setPickable(true);
        front.setCapability(Shape3D.ALLOW_PICKABLE_READ);
        front.setCapability(Shape3D.ALLOW_PICKABLE_WRITE);

        // 10. Set pickable for all cube faces
        box.getShape(Box.FRONT).setPickable(true);
        box.getShape(Box.BACK).setPickable(true);
        box.getShape(Box.LEFT).setPickable(true);
        box.getShape(Box.RIGHT).setPickable(true);
        box.getShape(Box.TOP).setPickable(true);
        box.getShape(Box.BOTTOM).setPickable(true);
        box.setBounds(bounds);
        box.setPickable(true);

        // 11. Add cube to the transform group
        tg.addChild(box);

        // 12. Start live texture update timer
        new Timer(33, e -> updateMazeTexture()).start();

        return root;
    }

    private void updateMazeTexture() {
        mazePanel.repaint();
        BufferedImage img = new BufferedImage(mazePanel.getWidth(), mazePanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        mazePanel.paint(g2d);
        g2d.dispose();
        imageComponent.set(img);
        dynamicTexture.setImage(0, imageComponent);
    }

    // Public getter for the generated cube TransformGroup.
    public TransformGroup getCubeTransformGroup() {
        // If in integrated mode, remove the cube from its parent (the integratedRoot) if it hasn't been removed yet.
        if (integratedRoot != null && cubeTransformGroup.getParent() != null) {
            integratedRoot.removeChild(cubeTransformGroup);
            integratedRoot = null; // Clear the reference so we don't attempt removal again.
        }
        return cubeTransformGroup;
    }

    public static void main(String[] args) {
        // Default mode: creates the full window and live scene.
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Java3DExample().setVisible(true);
            }
        });
    }
    public MazePanel getMazePanel() {
        return mazePanel;
    }

}
