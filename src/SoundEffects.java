import java.net.URL;
import org.jogamp.java3d.BackgroundSound;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.MediaContainer;
import org.jogamp.java3d.PointSound;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.java3d.utils.universe.Viewer;
import org.jogamp.vecmath.Point3d;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SoundEffects {
	 public static PointSound horrorBGSound = createSound("sounds/(Free) Horror Ambiance - Ominous Background Music.wav");
	 public static PointSound doorBGSound = createSound("sounds/DOOR Opening Sound Effect ( HD ) Copyright Free.wav");
	 public static PointSound correctPinBGSound = createSound("sounds/Success Sound Effect.wav");
	 public static PointSound wrongPinBGSound = createSound("sounds/Error - Sound Effect ｜ Non copyright sound effects ｜ FeeSou.wav");
	 public static PointSound heartbeatBGSound = createSound("sounds/Dramatic Heartbeat Sound Effect.wav");
	 
	 public static void playSound(PointSound sound) {
	        sound.setEnable(true);
	        Timer timer = new Timer(2000, new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                sound.setEnable(false);
	                ((Timer) e.getSource()).stop();
	            }
	        });
	        timer.setRepeats(false);
	        timer.start();
	    }
	 
	 public static PointSound createSound(String name) {
		 URL soundURL = locateSound(name);
	        if (soundURL == null) {
	            System.err.println("Error: Sound file not found: " + name);
	            return null;
	        }
	        MediaContainer pointMedia = new MediaContainer(soundURL);
	        pointMedia.setCacheEnable(true);

	        PointSound pointSound = new PointSound();
	        pointSound.setSoundData(pointMedia);
	        pointSound.setEnable(false);
	        pointSound.setInitialGain(0.5f);
	        pointSound.setCapability(PointSound.ALLOW_ENABLE_WRITE);
	        pointSound.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000));
	        return pointSound;
    }
	 
	public static URL locateSound(String fn) {
		return SoundEffects.class.getClassLoader().getResource("sounds/" + fn);

	}
	 
	 public static BackgroundSound bkgdSound() {
	     BackgroundSound bgSound = new BackgroundSound();
		 bgSound.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000));
		 return bgSound;
	 }
	 
	 public static void enableAudio(SimpleUniverse simple_U) {
	        Viewer viewer = simple_U.getViewer();
	        viewer.getView().setBackClipDistance(20.0f);
	}

}
