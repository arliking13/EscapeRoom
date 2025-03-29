package com.escaperoom.room3;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;
import org.jogamp.vecmath.Vector3f;  // Needed for DirectionalLight

public class Main {
    public static void main(String[] args) {
        // Create the universe
        SimpleUniverse universe = new SimpleUniverse();
        
        // Create the scene
        CreateObjects sceneCreator = new CreateObjects();
        BranchGroup scene = sceneCreator.createScene();
        
        // Set up the viewing platform
        setUpView(universe);
        
        // Add the scene to the universe
        universe.addBranchGraph(scene);
    }
    
    private static void setUpView(SimpleUniverse universe) {
        // This will move the ViewPlatform back a bit so the objects in the scene can be seen
        universe.getViewingPlatform().setNominalViewingTransform();
        
        // Set the view transform to look at the chair
        TransformGroup viewTransform = universe.getViewingPlatform().getViewPlatformTransform();
        Transform3D transform = new Transform3D();
        transform.lookAt(new Point3d(0, 0, 2), new Point3d(0, 0, 0), new Vector3d(0, 1, 0));
        transform.invert();
        viewTransform.setTransform(transform);
        
        // Set background color to light gray
        Background background = new Background(new Color3f(0.9f, 0.9f, 0.9f));
        background.setApplicationBounds(new BoundingSphere(new Point3d(), 100.0));
        
        // Create a BranchGroup for the background and add it
        BranchGroup bgBranchGroup = new BranchGroup();
        bgBranchGroup.addChild(background);
        universe.addBranchGraph(bgBranchGroup);
        
        // Add basic lighting
        addLights(universe);
    }
    
    private static void addLights(SimpleUniverse universe) {
        BranchGroup lightGroup = new BranchGroup();
        
        // Create ambient light
        AmbientLight ambientLight = new AmbientLight(new Color3f(0.5f, 0.5f, 0.5f));
        ambientLight.setInfluencingBounds(new BoundingSphere(new Point3d(), 100.0));
        lightGroup.addChild(ambientLight);
        
        // Create directional light - using Vector3f
        Vector3f lightDirection = new Vector3f(-1.0f, -1.0f, -1.0f);
        lightDirection.normalize();  // Important for directional lights
        DirectionalLight directionalLight = new DirectionalLight(
            new Color3f(1.0f, 1.0f, 1.0f),
            lightDirection);
        directionalLight.setInfluencingBounds(new BoundingSphere(new Point3d(), 100.0));
        lightGroup.addChild(directionalLight);
        
        universe.addBranchGraph(lightGroup);
    }
}