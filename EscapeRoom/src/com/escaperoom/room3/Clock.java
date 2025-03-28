package com.escaperoom.room3;

import javax.swing.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.geometry.ColorCube;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Vector3f;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Clock extends JFrame implements ActionListener {
	private String inputCode = "";
	private final String secretCode = "102";
	private JLabel display;
	
	public Clock() {
		setTitle("Enter the Code");
		setSize(400, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
     // 3D View
        Canvas3D canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        add(canvas3D, BorderLayout.CENTER);
        create3DScene(canvas3D);

        // Keypad
        JPanel keypadPanel = new JPanel(new GridLayout(4, 3));
        for (int i = 1; i <= 9; i++) {
            addButton(keypadPanel, String.valueOf(i));
        }
        addButton(keypadPanel, "0");
        addButton(keypadPanel, "Clear");
        addButton(keypadPanel, "Enter");
        add(keypadPanel, BorderLayout.SOUTH);

        // Display Panel
        display = new JLabel("Enter Code:", SwingConstants.CENTER);
        display.setFont(new Font("Arial", Font.BOLD, 18));
        add(display, BorderLayout.NORTH);
	}
	
	private void create3DScene(Canvas3D canvas) {
        SimpleUniverse universe = new SimpleUniverse(canvas);
        BranchGroup group = new BranchGroup();

        Transform3D transform = new Transform3D();
        transform.setTranslation(new Vector3f(0.0f, 0.0f, -5.0f));

        TransformGroup transformGroup = new TransformGroup(transform);
        transformGroup.addChild(new ColorCube(0.4));
        group.addChild(transformGroup);

        universe.getViewingPlatform().setNominalViewingTransform();
        universe.addBranchGraph(group);
    }

    private void addButton(JPanel panel, String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.addActionListener(this);
        panel.add(button);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("Clear")) {
            inputCode = "";
            display.setText("Enter Code:");
        } else if (command.equals("Enter")) {
            if (inputCode.equals(secretCode)) {
                display.setText("Unlocked!");
            } else {
                display.setText("Wrong Code!");
                inputCode = "";
            }
        } else {
            inputCode += command;
            display.setText("Code: " + inputCode);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Clock().setVisible(true));
    }
}
