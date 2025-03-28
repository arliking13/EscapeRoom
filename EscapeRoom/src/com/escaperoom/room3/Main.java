package com.escaperoom.room3;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.universe.*;
import org.jogamp.vecmath.*;

import java.awt.GraphicsConfiguration;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Room3...");
        printSystemInfo();

        try {
            SimpleUniverse universe = createUniverse();
            CreateObjects roomBuilder = new CreateObjects();
            
            System.out.println("\nAdding room to universe...");
            universe.addBranchGraph(roomBuilder.getRoom());
            
            setupViewer(universe);
            System.out.println("\nRoom3 successfully initialized!");
        } catch (Exception e) {
            System.err.println("Failed to initialize Room3:");
            e.printStackTrace();
        }
    }

    private static void printSystemInfo() {
        System.out.println("Working Directory: " + new File("").getAbsolutePath());
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("3D Acceleration: " + 
            System.getProperty("j3d.rend", "Not specified"));
    }

    private static SimpleUniverse createUniverse() {
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas = new Canvas3D(config);
        
        java.awt.Frame frame = new java.awt.Frame("Escape Room - Room 3");
        frame.setLayout(new java.awt.BorderLayout());
        frame.add(canvas, java.awt.BorderLayout.CENTER);
        frame.setSize(1024, 768);
        frame.setVisible(true);
        
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
        
        return new SimpleUniverse(canvas);
    }

    private static void setupViewer(SimpleUniverse universe) {
        ViewingPlatform vp = universe.getViewingPlatform();
        TransformGroup vtg = vp.getViewPlatformTransform();
        
        Transform3D viewTransform = new Transform3D();
        viewTransform.set(new Vector3f(0f, 1.5f, 5f));
        
        Transform3D lookAt = new Transform3D();
        lookAt.lookAt(
            new Point3d(0, 1.5, 5),  // Eye
            new Point3d(0, 0, 0),     // Center
            new Vector3d(0, 1, 0)     // Up
        );
        lookAt.invert();
        
        viewTransform.mul(lookAt);
        vtg.setTransform(viewTransform);
        
        // Configure view
        View view = universe.getViewer().getView();
        view.setFieldOfView(Math.toRadians(45));
        view.setBackClipDistance(20.0);
        view.setFrontClipDistance(0.1);
        
        System.out.println("Viewer configured successfully");
    }
}