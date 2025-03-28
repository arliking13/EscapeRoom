package com.escaperoom.room3;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.*;
import org.jogamp.vecmath.*;

import java.io.File;
import java.util.*;

public class CreateObjects {
    private static final String[] OBJECT_NAMES = {
        "Baseboard", "Ceiling_lamp", "ChairOld", "Cornice",
        "Cross_left", "Cross_middle", "Cross_right",
        "Desk", "Entrance_door", "Escape_door",
        "Locker", "Locker&door", "Paper",
        "SwitchHandle", "SwitchMain", "The_leftmost_cross",
        "The_rightmost_cross", "Wall_light_left", "Wall_light_right",
        "Window_Casement_Trame"
    };

    private final SharedGroup[] sharedGroups = new SharedGroup[OBJECT_NAMES.length];
    private final BranchGroup roomRoot = new BranchGroup();
    private final Map<String, TransformGroup> objectMap = new HashMap<>();

    public CreateObjects() {
        System.out.println("Initializing Room3 objects...");
        roomRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        roomRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        
        verifyResources();
        createSharedGroups();
        buildRoom();
    }

    private void verifyResources() {
        System.out.println("\nVerifying resource files:");
        for (String obj : OBJECT_NAMES) {
            String objFile = obj + ".obj";
            boolean found = false;
            
            for (String path : new String[]{
                "assets/room3/" + objFile,
                "assets/" + objFile,
                "src/assets/room3/" + objFile
            }) {
                if (new File(path).exists()) {
                    System.out.println("✓ " + path);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                System.err.println("✗ Missing: " + objFile);
            }
        }
    }

    private void createSharedGroups() {
        System.out.println("\nCreating shared groups...");
        for (int i = 0; i < OBJECT_NAMES.length; i++) {
            sharedGroups[i] = new SharedGroup();
            String objPath = OBJECT_NAMES[i] + ".obj";
            System.out.println("Loading: " + objPath);
            
            sharedGroups[i].addChild(LoadObject.loadObject(objPath));
            sharedGroups[i].setName(OBJECT_NAMES[i]);
            sharedGroups[i].compile();
        }
    }

    private void buildRoom() {
        System.out.println("\nBuilding room...");
        
        // Room structure
        addObject("!Baseboard", new Vector3d(0, -0.5, 0), 1.0);
        addObject("!Ceiling_lamp", new Vector3d(0, 2.5, 0), 0.5);
        
        // Walls and windows
        addObject("!Window_Casement_Trame", new Vector3d(4.5, 1.2, 0), 1.0, 
                 new AxisAngle4d(0, 1, 0, Math.PI/2));
        
        // Doors
        addObject("@Entrance_door", new Vector3d(-4.5, 0, 0), 1.0);
        addObject("@Escape_door", new Vector3d(4.5, 0, -3.0), 1.0);
        
        // Furniture
        addObject("!Desk", new Vector3d(-2.0, 0, 2.0), 0.8);
        addObject("!Desk", new Vector3d(2.0, 0, 2.0), 0.8);
        
        // Chairs
        addObject("!ChairOld", new Vector3d(-2.5, 0, 2.0), 0.7);
        addObject("!ChairOld", new Vector3d(1.5, 0, 2.5), 0.7, 
                 new AxisAngle4d(0, 1, 0, Math.PI/2));
        
        // Lockers
        addObject("!Locker", new Vector3d(1.0, 0, -2.0), 0.9);
        addObject("@Locker&door", new Vector3d(1.2, 0.8, -2.0), 0.9);
        
        // Wall lights
        addObject("!Wall_light_left", new Vector3d(-4.0, 1.8, 2.0), 0.5);
        addObject("!Wall_light_right", new Vector3d(4.0, 1.8, -2.0), 0.5);
        
        // Crosses
        addObject("!Cross_left", new Vector3d(-3.0, 1.5, 0), 0.4);
        addObject("!Cross_middle", new Vector3d(0, 1.5, 0), 0.4);
        addObject("!Cross_right", new Vector3d(3.0, 1.5, 0), 0.4);
        
        // Switches
        addObject("@SwitchMain", new Vector3d(3.8, 1.2, 0), 0.2);
        addObject("@SwitchHandle", new Vector3d(3.8, 1.2, 0.1), 0.2);
        
        // Papers
        addObject("#Paper", new Vector3d(-1.5, 0.8, 1.5), 0.3);
        
        // Cornice
        addObject("!Cornice", new Vector3d(0, 2.2, -3.5), 1.0);
    }

    private void addObject(String name, Vector3d position, double scale) {
        addObject(name, position, scale, new AxisAngle4d());
    }

    private void addObject(String name, Vector3d position, double scale, AxisAngle4d rotation) {
        String baseName = name.substring(1);
        TransformGroup tg = new TransformGroup();
        
        Transform3D transform = new Transform3D();
        transform.set(rotation);
        transform.setScale(scale);
        transform.setTranslation(position);
        
        tg.setTransform(transform);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg.setName(name);

        // Find matching shared group
        for (int i = 0; i < OBJECT_NAMES.length; i++) {
            if (OBJECT_NAMES[i].equals(baseName)) {
                tg.addChild(new Link(sharedGroups[i]));
                break;
            }
        }

        roomRoot.addChild(tg);
        objectMap.put(name, tg);
        System.out.println("Added: " + name);
    }

    public BranchGroup getRoom() {
        return roomRoot;
    }
}