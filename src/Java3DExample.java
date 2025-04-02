import com.example.labyrinth.MazePanel;
import com.example.labyrinth.MazeGameState;


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

    private Canvas3D canvas;
    private SimpleUniverse universe;
    private PickCanvas pickCanvas;
    private Clip clip;
    private Box box;
    private TransformGroup cubeTransformGroup;
    private MazeGameState gameState;



    // Maze-related
    private MazePanel mazePanel;
    private Texture2D dynamicTexture;
    private ImageComponent2D imageComponent;

    public Java3DExample() {
        setTitle("Java3D Cube with Live Maze Texture");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Canvas
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        add("Center", canvas);

        // ✅ Add this block RIGHT AFTER canvas is created
        canvas.setFocusable(true);
        canvas.requestFocusInWindow();

        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                mazePanel.dispatchEvent(e); // ✅ send to MazePanel (which has full logic)
            }
        });


   

        // Toggle Button
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

                            // ✅ You can add your own 'activateGame()' logic here if needed
                            // For example, toggle a 'gameActive' boolean
                            canvas.requestFocusInWindow(); // Ensure key listener works
                        }
                    }
                }

                lastClickTime = now;
            }
        });
    }
    
    

    
    
    private void playSound() {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    private void toggleProjection(boolean is2D) {
        View view = universe.getViewer().getView();
        if (is2D) {
            view.setProjectionPolicy(View.PARALLEL_PROJECTION);
        } else {
            view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
        }
    }

    private BranchGroup createSceneGraph() {
        BranchGroup root = new BranchGroup();

        // 1. Create and store transform group
        TransformGroup tg = new TransformGroup();
        cubeTransformGroup = tg;
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        root.addChild(tg);

        // 2. Mouse rotate
        MouseRotate rotate = new MouseRotate();
        rotate.setTransformGroup(tg);
        rotate.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        root.addChild(rotate);

        // 3. Bounds
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

        // 4. Lighting
        AmbientLight light = new AmbientLight(new Color3f(1.0f, 1.0f, 1.0f));
        light.setInfluencingBounds(bounds);
        root.addChild(light);

        // 5. Maze panel to image
        gameState = new MazeGameState();             // ✅ shared game logic
        mazePanel = new MazePanel(gameState);        // ✅ share game logic with texture

        mazePanel.setSize(800, 640);
        mazePanel.setDoubleBuffered(true);

        BufferedImage image = new BufferedImage(mazePanel.getWidth(), mazePanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        mazePanel.paint(g2d);
        g2d.dispose();

        // 6. Texture setup
        imageComponent = new ImageComponent2D(ImageComponent.FORMAT_RGBA, image);
        imageComponent.setCapability(ImageComponent.ALLOW_IMAGE_WRITE);

        dynamicTexture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
                image.getWidth(), image.getHeight());
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
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        mazeAppearance.setTextureAttributes(texAttr);

        // 7. Default appearance for other faces
        Appearance defaultApp = new Appearance();
        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(0.2f, 0.6f, 1.0f);
        defaultApp.setColoringAttributes(ca);

        // 8. Now safely create the cube
        box = new Box(0.4f, 0.25f, 0.02f, Primitive.GENERATE_TEXTURE_COORDS, defaultApp);  // Smaller cube



        // 9. Assign the textured face
        box.getShape(Box.FRONT).setAppearance(mazeAppearance);
     // Ensure FRONT face is fully pickable and responds to clicks
        box.setCapability(Box.ALLOW_PICKABLE_READ);
        box.setCapability(Box.ALLOW_PICKABLE_WRITE);
        box.setPickable(true);

        Shape3D front = box.getShape(Box.FRONT);
        front.setPickable(true);
        front.setCapability(Shape3D.ALLOW_PICKABLE_READ);
        front.setCapability(Shape3D.ALLOW_PICKABLE_WRITE);




        // 10. Set pickable for all faces
        box.getShape(Box.FRONT).setPickable(true);
        box.getShape(Box.BACK).setPickable(true);
        box.getShape(Box.LEFT).setPickable(true);
        box.getShape(Box.RIGHT).setPickable(true);
        box.getShape(Box.TOP).setPickable(true);
        box.getShape(Box.BOTTOM).setPickable(true);
        box.setBounds(bounds);
        box.setPickable(true);

        // 11. Add to scene
        tg.addChild(box);

        // 12. Start live texture update
        new Timer(33, e -> updateMazeTexture()).start();

        return root;
    }

    
    
    
    
    
    private void updateMazeTexture() {
    	mazePanel.repaint(); // ✅ trigger paint() to update content

        // ✅ This method should end cleanly like this
        BufferedImage img = new BufferedImage(mazePanel.getWidth(), mazePanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        mazePanel.paint(g2d);
        g2d.dispose();

        imageComponent.set(img);
        dynamicTexture.setImage(0, imageComponent);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Java3DExample().setVisible(true);
            }
        });
    }
} // ✅ final class brace here