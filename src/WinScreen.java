import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class WinScreen extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public WinScreen(JFrame frame) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setBorder(new EmptyBorder(50, 30, 50, 30));

        // Main victory message
        JLabel titleLabel = new JLabel("ESCAPE SUCCESSFUL", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Courier New", Font.BOLD, 48));
        titleLabel.setForeground(new Color(0xE74C3C)); // Blood red
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));

        // Subtext with typewriter effect
        JTextArea subtext = new JTextArea();
        subtext.setEditable(false);
        subtext.setLineWrap(true);
        subtext.setWrapStyleWord(true);
        subtext.setBackground(Color.BLACK);
        subtext.setForeground(Color.WHITE);
        subtext.setFont(new Font("Courier New", Font.PLAIN, 18));
        subtext.setText("The door creaks open as you stumble into the moonlight...\n\n" +
                      "The nightmare is over. For now.");

        // Add components
        add(titleLabel, BorderLayout.NORTH);
        add(subtext, BorderLayout.CENTER);

        // Play victory sound (reuse your sound system)
        SoundEffects.playSound(SoundEffects.correctPinBGSound);
    }
}