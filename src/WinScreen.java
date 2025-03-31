import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class WinScreen extends Screen {
	private static final long serialVersionUID = 1L;

	@Override
	    public void paintComponent(Graphics g) {
	        g.setColor(Color.BLACK);
	        g.fillRect(0, 0, getWidth(), getHeight());
	        g.setColor(Color.WHITE);
	        g.setFont(new Font("Arial", Font.BOLD, 50));
	        g.drawString("You escaped from the Haunted House this time!", getWidth() / 2 - 150, getHeight() / 2 + 50);
	    }
}
