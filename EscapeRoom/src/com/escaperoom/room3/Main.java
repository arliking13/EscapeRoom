package com.escaperoom.room3;

import java.awt.GraphicsConfiguration;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.universe.*;
import org.jogamp.vecmath.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting 3D Viewer...");
        
        // Setup graphics
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas = new Canvas3D(config);
        
        // Create frame
        java.awt.Frame frame = new java.awt.Frame("3D Object Viewer");
        frame.setLayout(new java.awt.BorderLayout());
        frame.add(canvas, java.awt.BorderLayout.CENTER);
        frame.setSize(1024, 768);
        frame.setVisible(true);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
        
        // Create universe
        SimpleUniverse universe = new SimpleUniverse(canvas);
        
        // Setup viewer
        setupViewer(universe);
        
        // Add scene
        CreateObjects scene = new CreateObjects();
        universe.addBranchGraph(scene.getScene());
        
        System.out.println("Viewer ready");
    }

    private static void setupViewer(SimpleUniverse universe) {
        ViewingPlatform viewingPlatform = universe.getViewingPlatform();
        TransformGroup vtg = viewingPlatform.getViewPlatformTransform();
        
        // Position viewer
        Transform3D viewTransform = new Transform3D();
        viewTransform.set(new Vector3f(0f, 1f, 5f));
        
        // Look at center
        Transform3D lookAt = new Transform3D();
        lookAt.lookAt(
            new Point3d(0, 0, 5),
            new Point3d(0, 0, 0), 
            new Vector3d(0, 1, 0)
        );
        lookAt.invert();
        viewTransform.mul(lookAt);
        
        vtg.setTransform(viewTransform);
        
        // Configure view
        View view = universe.getViewer().getView();
        view.setFieldOfView(Math.toRadians(45));
        view.setBackClipDistance(50.0);
    }
}