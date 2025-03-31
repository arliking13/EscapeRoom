import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.universe.*;
import org.jogamp.vecmath.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import javax.swing.JFrame;
import java.awt.event.*;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Enumeration;

public class Main {
    // Sensitivity controls
    private static final float MOUSE_SENSITIVITY = 0.0008f;
    private static final float MOVEMENT_SPEED = 0.009f;
    private static final float VERTICAL_SPEED = 0.04f;
    
    private static PlayerControls playerControls;
    private static boolean mouseCaptured = true;
    private static GameCanvas canvas;
    private static SimpleUniverse universe;
    
    public static void main(String[] args) {
        try {
            initialize3DEnvironment();
            BranchGroup scene = createScene();
            universe.addBranchGraph(scene);
            startGameLoop();
        } catch (Exception e) {
            System.err.println("Initialization failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void initialize3DEnvironment() throws AWTException {
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        canvas = new GameCanvas();
        canvas.setPreferredSize(new Dimension(1280, 720));
        
        JFrame frame = new JFrame("3D Escape Room with Cross Puzzle");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        
        universe = new SimpleUniverse(canvas);
        configureViewingPlatform();
        
        playerControls = new PlayerControls(
            universe.getViewingPlatform().getViewPlatformTransform(),
            canvas,
            MOUSE_SENSITIVITY,
            MOVEMENT_SPEED,
            VERTICAL_SPEED
        );
        
        // Initialize sound system
        SoundEffects.enableAudio(universe);
        
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
        LoadObject.setObjectTexture("KeypadDoorLock", "KeypadDoorLockAlbedo.jpg");
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
        LoadObject.setObjectTexture("Window_Casement_Frame", "Door_Wood_Dif.jpg");
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
            scene.addChild(creator.createObject("ChairOld", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0.5, -0.3, -0.2), 0.2));
            scene.addChild(creator.createObject("Desk", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0.5, -0.28, 0.1), 0.3));
            scene.addChild(creator.createObject("Locker", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0.6, -0.2, 0.6), 0.2));
            scene.addChild(creator.createObject("Door", new AxisAngle4d(0, 1, 0, Math.PI/2), new Vector3d(0.298, -0.2, -0.8), 0.9));
            scene.addChild(creator.createObject("Escape_door", new AxisAngle4d(0, 1, 0, 0), new Vector3d(-0.4, -0.2, 0.3), 0.5));
            scene.addChild(creator.createObject("Window_Casement_Frame", new AxisAngle4d(0, 1, 0, 0), new Vector3d(3.5, 1, 0), 1.0));
            scene.addChild(creator.createObject("Baseboard", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0, -0.5, 0), 1.0));
            scene.addChild(creator.createObject("Cornice", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0, -0.5, 0), 1.0));
            scene.addChild(creator.createObject("Cornice", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0, 0.4, 0), 1.0));
            scene.addChild(creator.createObject("Wall_light_left", new AxisAngle4d(0, 1, 0, 0), new Vector3d(-0.18, 0.2, 0.4), 0.7));
            scene.addChild(creator.createObject("Wall_light_right", new AxisAngle4d(0, 1, 0, 0), new Vector3d(-0.18, 0.2, 0.3), 0.7));
            scene.addChild(creator.createObject("Ceiling_lamp", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0, 0.25, 0.3), 0.06));
            scene.addChild(creator.createObject("Paper", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0.7, -0.1, 0.6), 0.2));
            scene.addChild(creator.createObject("SwitchMain", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0.1, -0.15, -0.3), 0.2));
            scene.addChild(creator.createObject("SwitchHandle", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0.1, -0.15, -0.3), 0.2));
            scene.addChild(creator.createObject("KeypadDoorLock", new AxisAngle4d(0, 1, 0, 0), new Vector3d(-0.7, -0.3, 0.1), 0.2));
            scene.addChild(creator.createObject("Lockers_door", new AxisAngle4d(0, 1, 0, 0), new Vector3d(-2, 0.5, 1.5), 0.5));
            
//crosses
                scene.addChild(creator.createObject("The_leftmost_cross", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0.95, -0.09, -0.23), 0.25));
                scene.addChild(creator.createObject("Cross_left", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0.95, 0.03, -0.19), 0.17));
                scene.addChild(creator.createObject("Cross_middle", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0.95, -0.08, -0.14), 0.09));
                scene.addChild(creator.createObject("Cross_right", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0.95, 0.025, -0.039), 0.07));
                scene.addChild(creator.createObject("The_rightmost_cross", new AxisAngle4d(0, 1, 0, 0), new Vector3d(0.95, -0.08, 0.13), 0.07));

        } catch (Exception e) {
            System.err.println("Error creating scene objects: " + e.getMessage());
            return createErrorScene();
        }
        
        scene.addChild(createEnhancedLights());
        return scene;
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
}