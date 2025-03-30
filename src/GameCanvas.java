import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.utils.universe.SimpleUniverse;

import java.awt.Font;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Point;

public class GameCanvas extends Canvas3D {
    private static final long serialVersionUID = 1L;
    private static Color cursorColor = Color.WHITE;
    private boolean showCrosshair = true;
    
    public GameCanvas() {
        super(SimpleUniverse.getPreferredConfiguration());
        setCursor(getToolkit().createCustomCursor(
            new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
            new Point(0, 0),
            "hidden"
        ));
    }

    public void postRender() {
        if (showCrosshair) {
            this.getGraphics2D().setColor(cursorColor);
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            
            // Horizontal line
            this.getGraphics2D().fillRect(centerX - 10, centerY - 1, 20, 2);
            // Vertical line
            this.getGraphics2D().fillRect(centerX - 1, centerY - 10, 2, 20);
            // Center dot
            this.getGraphics2D().fillRect(centerX - 1, centerY - 1, 2, 2);
            
            this.getGraphics2D().flush(false);
        }
    }

    public void toggleCrosshair(boolean show) {
        this.showCrosshair = show;
    }

    public static void setCursorColor(Color color) {
        cursorColor = color;
    }

    public void togglePause(boolean paused) {
        if (paused) {
            this.getGraphics2D().setColor(new Color(0, 0, 0, 0.9f));
            this.getGraphics2D().fillRect(0, 0, getWidth(), getHeight());
            this.getGraphics2D().setColor(Color.white);
            this.getGraphics2D().setFont(new Font("Arial", Font.PLAIN, 40));
            this.getGraphics2D().drawString("Paused", getWidth() / 2 - 50, getHeight() / 2);
        } else {
            this.getGraphics2D().setColor(new Color(0, 0, 0, 0.0f));
            this.getGraphics2D().fillRect(0, 0, getWidth(), getHeight());
        }
        this.getGraphics2D().flush(false);
    }
}