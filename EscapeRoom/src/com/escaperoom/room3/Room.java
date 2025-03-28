package com.escaperoom.room3;

import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.loaders.Scene;
import javax.media.j3d.*;
import com.sun.j3d.utils.universe.SimpleUniverse;
import javax.vecmath.*;

import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.TransformGroup;

import java.io.FileReader;

public class Room {
    public static void main(String[] args) {
        // Create a universe and a viewing platform
        SimpleUniverse universe = new SimpleUniverse();
        BranchGroup scene = new BranchGroup();

        // Load the .obj file
        ObjectFile loader = new ObjectFile(ObjectFile.RESIZE);
        Scene modelScene = null;

        try {
            modelScene = loader.load(new FileReader("room.obj"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (modelScene != null) {
            TransformGroup tg = new TransformGroup();
            tg.addChild(modelScene.getSceneGroup());
            scene.addChild(tg);
        }

        // Set up lighting
        addLight(scene);

        universe.getViewingPlatform().setNominalViewingTransform();
        universe.addBranchGraph(scene);
    }

    private static void addLight(BranchGroup scene) {
        Color3f lightColor = new Color3f(1.0f, 1.0f, 1.0f);
        BoundingSphere bounds = new BoundingSphere();
        DirectionalLight light = new DirectionalLight(lightColor, new Vector3f(-1.0f, -1.0f, -1.0f));
        light.setInfluencingBounds(bounds);
        scene.addChild(light);
    }
}

