package com.escaperoom.room1;

import com.escaperoom.maze.MazePanel;

import javax.swing.*;
import java.awt.*;
import javax.media.j3d.*;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.geometry.Box;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

public class MazeOnTV {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Escape Room with Maze TV");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null); // Absolute layout

        // Java3D Canvas
        Canvas3D canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        canvas3D.setBounds(0, 0, 800, 600);
        frame.add(canvas3D);

        // MazePanel overlaid as "TV screen"
        MazePanel mazePanel = new MazePanel();
        mazePanel.setBounds(500, 200, 300, 300); // Adjust to match your TV object visually
        mazePanel.setOpaque(false);
        frame.add(mazePanel);

        // Java3D scene setup
        SimpleUniverse universe = new SimpleUniverse(canvas3D);
        BranchGroup scene = new BranchGroup();

        // Lighting
        BoundingSphere bounds = new BoundingSphere();
        Color3f lightColor = new Color3f(1f, 1f, 1f);
        Vector3f lightDir = new Vector3f(0f, 0f, -1f);
        DirectionalLight light = new DirectionalLight(lightColor, lightDir);
        light.setInfluencingBounds(bounds);
        scene.addChild(light);

        // TV Object in 3D
        Appearance tvAppearance = new Appearance();
        Box tvBox = new Box(0.3f, 0.2f, 0.02f, tvAppearance);

        Transform3D tvTransform = new Transform3D();
        tvTransform.setTranslation(new Vector3f(0.0f, 0.0f, -1.0f)); // In front of camera
        TransformGroup tvGroup = new TransformGroup(tvTransform);
        tvGroup.addChild(tvBox);
        scene.addChild(tvGroup);

        // Finalize scene
        universe.getViewingPlatform().setNominalViewingTransform();
        universe.addBranchGraph(scene);

        // Show window
        frame.setSize(800, 600);
        frame.setVisible(true);
        mazePanel.requestFocusInWindow(); // So it captures keys
    }
}
