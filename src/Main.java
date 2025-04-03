import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.geometry.Sphere;
import org.jogamp.java3d.utils.universe.*;
import org.jogamp.vecmath.*;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.ActionEvent;



import java.awt.event.*;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Enumeration;
import org.jogamp.java3d.utils.picking.PickTool;
import org.jogamp.java3d.utils.picking.PickResult;
import org.jogamp.java3d.utils.picking.PickCanvas;

public class Main {
    // Sensitivity controls
    private static final float MOUSE_SENSITIVITY = 0.0008f;
    private static final float MOVEMENT_SPEED = 0.009f;
    private static final float VERTICAL_SPEED = 0.04f;
    
    private static PlayerControls playerControls;
    private static boolean mouseCaptured = true;
    private static GameCanvas canvas;
    private static SimpleUniverse universe;
    private static TransformGroup ceilingLampTransform;
    private static boolean justADoorOpened = false;
    private static boolean mazeActive = false;
    private static MazePanel integratedMazePanel;




    
    public static void main(String[] args) {
        try {
            initialize3DEnvironment();
            BranchGroup scene = createScene();
            universe.addBranchGraph(scene);
            setupPicking();
            startGameLoop();
        } catch (Exception e) {
            System.err.println("Initialization failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("🔚 Shutting down: Cleaning up OpenAL");
            SoundEffects.cleanup();
        }));

    }
    


    private static void setupPicking() {
        PickCanvas pickCanvas = new PickCanvas(canvas, universe.getLocale());
        pickCanvas.setMode(PickTool.GEOMETRY);
        pickCanvas.setTolerance(4.0f);

        canvas.addMouseListener(new MouseAdapter() {
            private long lastClickTime = 0;
            @Override
            public void mouseClicked(MouseEvent e) {
                long now = System.currentTimeMillis();
                if (now - lastClickTime < 300) { // Double-click detected
                    pickCanvas.setShapeLocation(e);
                    PickResult[] results = pickCanvas.pickAll();
                    for (PickResult result : results) {
                        Node node = result.getObject();
                        if (node != null) {
                            Node parent = node.getParent();
                            while (parent != null) {
                                if (parent instanceof TransformGroup) {
                                    TransformGroup tg = (TransformGroup) parent;
                                    if (tg.getUserData() != null) {
                                        String objName = (String) tg.getUserData();
                                        if (objName.equals("MazeObject")) {
                                            if (!mazeActive) {
                                                System.out.println("ENTER: Maze object double-clicked!");
                                                mazeActive = true;

                                                // ✅ Set focus explicitly
                                             // ✅ Set focus explicitly
                                                integratedMazePanel.setFocusable(true);

                                                Timer t = new Timer(100, evt -> {
                                                    integratedMazePanel.setVisible(true); // only if using glass pane
                                                    boolean ok = integratedMazePanel.requestFocusInWindow();
                                                    System.out.println("✅ Delayed focus: " + ok);
                                                });

                                                t.setRepeats(false);
                                                t.start();



                                            } else {
                                                System.out.println("EXIT: Maze object double-clicked!");
                                                mazeActive = false;

                                                // Return focus to canvas
                                                canvas.requestFocusInWindow();
                                            }
                                            return;
                                        }

                                    }
                                }
                                parent = parent.getParent();
                            }
                        }
                    }
                }
                lastClickTime = now;
            }
        });
    }





    
    private static void initialize3DEnvironment() throws AWTException {
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        canvas = new GameCanvas();
        canvas.setPreferredSize(new Dimension(1280, 720));
        
        // Create the main frame and add the canvas to its content pane.
        JFrame frame = new JFrame("3D Escape Room with Cross Puzzle");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(canvas, BorderLayout.CENTER);
        
        // Set up the MazePanel as the glass pane.
        // If it hasn't been created yet, instantiate it.
        if (integratedMazePanel == null) {
            MazeGameState state = new MazeGameState();
            integratedMazePanel = new MazePanel(state);
            integratedMazePanel.setPreferredSize(new Dimension(1280, 720));
        }
        // Install the MazePanel as the glass pane and keep it hidden initially.
        frame.setGlassPane(integratedMazePanel);
        integratedMazePanel.setVisible(true); // <-- THIS is critical
        
        frame.pack();
        frame.setVisible(true);
        
        // Create the universe and configure the viewing platform.
        universe = new SimpleUniverse(canvas);
        configureViewingPlatform();
        
        // Create and install the player controls.
        playerControls = new PlayerControls(
            universe.getViewingPlatform().getViewPlatformTransform(),
            canvas,
            MOUSE_SENSITIVITY,
            MOVEMENT_SPEED,
            VERTICAL_SPEED
        );
                
        customizeTextures();
    }




    private static class PlayerControls implements KeyListener, MouseMotionListener {
        private final TransformGroup viewTransformGroup;
        private final Transform3D transform = new Transform3D();
        private final Vector3f position = new Vector3f(0.0f, 0.0f, 5.0f);
        private float yaw = 0.0f;
        private float pitch = 0.0f;
        
        private final float mouseSensitivity;
        private final float moveSpeed;
        private final float verticalSpeed;
        
        private boolean forward, backward, left, right, up, down;
        private final Robot robot;
        private final GameCanvas canvas;
        
        public PlayerControls(TransformGroup tg, GameCanvas canvas, 
                            float mouseSensitivity, float moveSpeed, 
                            float verticalSpeed) throws AWTException {
            this.viewTransformGroup = tg;
            this.canvas = canvas;
            this.mouseSensitivity = mouseSensitivity;
            this.moveSpeed = moveSpeed;
            this.verticalSpeed = verticalSpeed;
            this.robot = new Robot();
            
            viewTransformGroup.getTransform(transform);
            updateTransform();
            
            canvas.addKeyListener(this);
            canvas.addMouseMotionListener(this);
            canvas.requestFocus();
            centerMouse();
        }
        
        private void centerMouse() {
            if (mouseCaptured && robot != null) {
                Point canvasLoc = canvas.getLocationOnScreen();
                robot.mouseMove(
                    canvasLoc.x + canvas.getWidth()/2,
                    canvasLoc.y + canvas.getHeight()/2
                );
            }
        }
        
        public void update() {
            Vector3f moveDir = new Vector3f();
            
            if (forward || backward || left || right) {
                Vector3f forwardDir = new Vector3f(
                    (float)Math.sin(yaw),
                    0,
                    (float)Math.cos(yaw)
                );
                forwardDir.normalize();
                
                Vector3f rightDir = new Vector3f();
                rightDir.cross(forwardDir, new Vector3f(0, 1, 0));
                rightDir.normalize();
                
                if (forward) moveDir.add(forwardDir);
                if (backward) moveDir.sub(forwardDir);
                if (right) moveDir.add(rightDir);
                if (left) moveDir.sub(rightDir);
                
                if (moveDir.length() > 0) {
                    moveDir.normalize();
                    moveDir.scale(moveSpeed);
                }
            }
            
            position.x += moveDir.x;
            position.z += moveDir.z;
            
            if (up) position.y += verticalSpeed;
            if (down) position.y -= verticalSpeed;
            
            position.y = Math.max(0.0f, position.y);
            
            updateTransform();
        }
        
        private void updateTransform() {
            pitch = Math.max(-(float)Math.PI/3, Math.min((float)Math.PI/3, pitch));
            
            Transform3D pitchRot = new Transform3D();
            Transform3D yawRot = new Transform3D();
            pitchRot.rotX(pitch);
            yawRot.rotY(yaw);
            
            transform.setIdentity();
            transform.mul(yawRot);
            transform.mul(pitchRot);
            transform.setTranslation(position);
            
            viewTransformGroup.setTransform(transform);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (Main.isMazeActive()) return; // ✅ ignore camera movement during maze

            switch (e.getKeyCode()) {

                case KeyEvent.VK_S: forward = true; break;
                case KeyEvent.VK_W: backward = true; break;
                case KeyEvent.VK_D: left = true; break;
                case KeyEvent.VK_A: right = true; break;
                case KeyEvent.VK_SPACE: up = true; break;
                case KeyEvent.VK_SHIFT: down = true; break;
                case KeyEvent.VK_ESCAPE: 
                    mouseCaptured = !mouseCaptured;
                    centerMouse();
                    break;
            }
        }


        @Override
        public void keyReleased(KeyEvent e) {
            if (Main.isMazeActive()) return; // ✅ ignore key releases during maze

            switch (e.getKeyCode()) {

                case KeyEvent.VK_S: forward = false; break;
                case KeyEvent.VK_W: backward = false; break;
                case KeyEvent.VK_D: left = false; break;
                case KeyEvent.VK_A: right = false; break;
                case KeyEvent.VK_SPACE: up = false; break;
                case KeyEvent.VK_SHIFT: down = false; break;
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (!mouseCaptured) return;
            
            Point currentPos = e.getPoint();
            int centerX = canvas.getWidth()/2;
            int centerY = canvas.getHeight()/2;
            
            int dx = currentPos.x - centerX;
            int dy = currentPos.y - centerY;
            
            if (dx != 0 || dy != 0) {
                yaw -= dx * mouseSensitivity;
                pitch -= dy * mouseSensitivity;
                centerMouse();
            }
        }
        
        @Override public void keyTyped(KeyEvent e) {}
        @Override public void mouseDragged(MouseEvent e) {}
    }

    private static void customizeTextures() {
        LoadObject.setObjectTexture("Baseboard", "Door_Wood_Dif.jpg");
        LoadObject.setObjectTexture("Ceiling_lamp", "steel_handle.jpg");
        LoadObject.setObjectTexture("ChairOld", "TreeLogEdgeWeathered.jpg");
        LoadObject.setObjectTexture("Cornice", "Curves.jpg");
        LoadObject.setObjectTexture("Cross_left", "Door_Wood_Dif.jpg");
        LoadObject.setObjectTexture("Cross_middle", "Door_Wood_Dif.jpg");
        LoadObject.setObjectTexture("Cross_right", "Door_Wood_Dif.jpg");
        LoadObject.setObjectTexture("Desk", "TreeLogEdgeWeathered.jpg");
        LoadObject.setObjectTexture("Door", "Door.jpg");
        LoadObject.setObjectTexture("Escape_door", "Door.jpg");
        LoadObject.setObjectTexture("Locker", "Blue_locker.jpg");
        LoadObject.setObjectTexture("Lockers_door", "rust_walls.jpg");
        LoadObject.setObjectTexture("Paper", "paper.jpg");
        LoadObject.setObjectTexture("room3", "rust_walls.jpg");
        LoadObject.setObjectTexture("SwitchHandle", "SwitchHandleAlbedo.jpg");
        LoadObject.setObjectTexture("SwitchMain", "SwitchMainAlbedo.jpg");
        LoadObject.setObjectTexture("The_leftmost_cross", "Door_Wood_Dif.jpg");
        LoadObject.setObjectTexture("The_rightmost_cross", "Door_Wood_Dif.jpg");
        LoadObject.setObjectTexture("Wall_light_left", "rust_walls.jpg");
        LoadObject.setObjectTexture("Wall_light_right", "rust_walls.jpg");
    }
    
    private static void configureViewingPlatform() {
        ViewingPlatform vp = universe.getViewingPlatform();
        View view = universe.getViewer().getView();
        
        view.setBackClipDistance(100.0);
        view.setFrontClipDistance(0.1);
        view.setFieldOfView(Math.toRadians(60));
        
        vp.setNominalViewingTransform();
        view.setScreenScalePolicy(View.SCALE_EXPLICIT);
        view.setScreenScale(1.0);
        
        TransformGroup vtg = vp.getViewPlatformTransform();
        Transform3D t3d = new Transform3D();
        vtg.getTransform(t3d);
        t3d.setTranslation(new Vector3f(0, 0.5f, 5.0f));
        vtg.setTransform(t3d);
    }
    
    private static BranchGroup createScene() {
        BranchGroup scene = new BranchGroup();
        scene.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        
        try {
            CreateObjects creator = new CreateObjects();
            
            // Create all scene objects
            scene.addChild(creator.createObject("room3", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0, 0, 0), 1.0));
            scene.addChild(creator.createObject("ChairOld", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0.5, -0.3, -0.4), 0.3));
            scene.addChild(creator.createObject("Desk", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0.5, -0.28, -0.4), 0.3));
            scene.addChild(creator.createObject("Locker", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0.6, -0.2, 0.6), 0.2));
            
            // Door with user data for sound
            TransformGroup door = creator.createObject("Door", new AxisAngle4d(0, 1, 0, Math.PI/2), new Vector3d(0.2, -0.2, -0.8), 0.8);
            door.setUserData("Door");
            scene.addChild(door);
            
            TransformGroup escapeDoor = creator.createObject("Escape_door", new AxisAngle4d(0, 1, 0, 0), new Vector3d(-0.40, -0.215, 0.49), 0.55);
            escapeDoor.setUserData("Escape_door"); // ✅ This is required for pick detection
            scene.addChild(escapeDoor);

            scene.addChild(creator.createObject("Baseboard", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0, -0.5, 0), 1.0));
            scene.addChild(creator.createObject("Cornice", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0, -0.5, 0), 1.0));
            scene.addChild(creator.createObject("Cornice", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0, 0.4, 0), 1.0));
            scene.addChild(creator.createObject("Wall_light_right", new AxisAngle4d(0, 1, 0, 0), new Vector3d(-0.16, 0.15, 0.45), 0.79));
            
            // Store reference to ceiling lamp transform
            ceilingLampTransform = creator.createObject("Ceiling_lamp", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0, 0.25, 0.3), 0.06);
            scene.addChild(ceilingLampTransform);
            
            scene.addChild(creator.createObject("Paper", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0.7, -0.1, 0.6), 0.2));
            scene.addChild(creator.createObject("SwitchMain", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0.1, -0.15, -0.42), 0.4));
            scene.addChild(creator.createObject("SwitchHandle", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0.1, -0.15, -0.42), 0.4));
            scene.addChild(creator.createObject("Lockers_door", new AxisAngle4d(0, 1, 0, 0), new Vector3d(-2, 0.5, 1.5), 0.5));
            scene.addChild(creator.createObject("The_leftmost_cross", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0.95, -0.09, -0.23), 0.25));
            scene.addChild(creator.createObject("Cross_left", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0.95, 0.03, -0.19), 0.17));
            
            // Middle cross with user data for sound
            TransformGroup middleCross = creator.createObject("Cross_middle", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0.95, -0.08, -0.14), 0.09);
            middleCross.setUserData("Cross_middle");
            scene.addChild(middleCross);
            
            scene.addChild(creator.createObject("Cross_right", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0.95, 0.025, -0.039), 0.07));
            scene.addChild(creator.createObject("The_rightmost_cross", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0.95, -0.08, 0.13), 0.07));

        } catch (Exception e) {
            System.err.println("Error creating scene objects: " + e.getMessage());
            return createErrorScene();
        }
   
 
     // // Integration of the Java3DExample maze object:
        Java3DExample example = new Java3DExample(true);
        TransformGroup cubeTG = example.getCubeTransformGroup();
        cubeTG.setUserData("MazeObject");

        // 1. Create rotation (rotate around Y axis)
        Transform3D rotation = new Transform3D();
        rotation.rotY(Math.PI / 2);  // 90 degrees (can also try Math.PI, etc.)

        // 2. Create translation
        Transform3D translation = new Transform3D();
        translation.setTranslation(new Vector3d(0.2, 0, -0.96));
        //x – left/right

        //y – up/down

        //z – forward/backward (depth)

        // 3. Combine rotation then translation
        rotation.mul(translation);  // Rotate, then move

        // 4. Apply to the object
        cubeTG.setTransform(rotation);

        scene.addChild(cubeTG);

        // Save MazePanel reference.
        integratedMazePanel = example.getMazePanel();
        integratedMazePanel.setVisible(false);
        canvas.requestFocusInWindow();
        canvas.requestFocusInWindow();

     // ✅ Forward arrow key events from Canvas to MazePanel when active
     canvas.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e) {
             if (mazeActive && integratedMazePanel != null) {
                 integratedMazePanel.dispatchEvent(e);
             }
         }

         @Override
         public void keyReleased(KeyEvent e) {
             if (mazeActive && integratedMazePanel != null) {
                 integratedMazePanel.dispatchEvent(e);
             }
         }
     });



        scene.addChild(createEnhancedLights());
        return scene;
    }
    
    // Modify your endGame method
    public static void endGame(boolean escaped) {
        if (escaped) {
            canvas.setVisible(true);
            showWinScreen();
            SoundEffects.play("Door_Open");
        }
    }

   

    // Add this new method
    private static void showWinScreen() {
        SwingUtilities.invokeLater(() -> {
            Container container = new Container();
			// Remove all components
            container.removeAll();
            
            Frame frame = new Frame();
			// Add win screen
            frame.add(new WinScreen(null));
            frame.validate();
            frame.repaint();
        });
    }
    
    private static BranchGroup createErrorScene() {
        BranchGroup errorScene = new BranchGroup();
        Appearance app = new Appearance();
        app.setMaterial(new Material(
            new Color3f(1f, 0f, 0f),
            new Color3f(0f, 0f, 0f),
            new Color3f(1f, 0f, 0f),
            new Color3f(1f, 1f, 1f),
            80.0f));
        
        errorScene.addChild(new Box(1f, 1f, 1f, app));
        return errorScene;
    }
    
    private static BranchGroup createEnhancedLights() {
        BranchGroup lightGroup = new BranchGroup();
        BoundingSphere bounds = new BoundingSphere(new Point3d(), 100.0);
        
        AmbientLight ambient = new AmbientLight(new Color3f(0.5f, 0.5f, 0.5f));
        ambient.setInfluencingBounds(bounds);
        lightGroup.addChild(ambient);
        
        DirectionalLight directional = new DirectionalLight(
            new Color3f(0.8f, 0.8f, 0.8f),
            new Vector3f(-0.5f, -1f, -0.5f));
        directional.setInfluencingBounds(bounds);
        lightGroup.addChild(directional);
        
        return lightGroup;
    }
    
    private static void startGameLoop() {
        new Thread(() -> {
            while (true) {
                try {
                    playerControls.update();
                    canvas.postRender();
                    Thread.sleep(16);
                } catch (Exception e) {
                    System.err.println("Game loop error: " + e.getMessage());
                }
            }
        }).start();
    }
    public static boolean isMazeActive() {
        return mazeActive;
    }

    public static void exitMazeMode() {
        mazeActive = false;
        integratedMazePanel.setVisible(false);
        canvas.requestFocusInWindow();
    }


}
