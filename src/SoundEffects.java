import java.net.URL;
import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.java3d.utils.universe.Viewer;
import org.jogamp.vecmath.Point3d;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SoundEffects {
    public static BackgroundSound horrorBGSound = createBackgroundSound("sounds/Horror_Ambiance.wav");
    public static BackgroundSound doorBGSound = createBackgroundSound("sounds/Door_Open.wav");
    public static BackgroundSound correctPinBGSound = createBackgroundSound("sounds/Success_Sound.wav");
    public static BackgroundSound wrongPinBGSound = createBackgroundSound("sounds/Error_Sound.wav");
    public static BackgroundSound heartbeatBGSound = createBackgroundSound("sounds/Heartbeat.wav");
    public static BackgroundSound crossRotationSound = createBackgroundSound("sounds/Success_Sound.wav");
    
    public static void playSound(PointSound sound) {
        if (sound == null) return;
        
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
    
    public static URL locateSound(String fn) {
        return SoundEffects.class.getClassLoader().getResource(fn);
    }
    
    public static PointSound createPointSound(String name) {
        URL soundURL = locateSound(name);
        if (soundURL == null) {
            System.err.println("Error: PointSound file not found: " + name);
            return null;
        }
        
        MediaContainer media = new MediaContainer(soundURL);
        media.setCacheEnable(true);
        
        PointSound sound = new PointSound();
        sound.setSoundData(media);
        sound.setInitialGain(1.0f);
        sound.setCapability(PointSound.ALLOW_ENABLE_WRITE);
        sound.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000));
        sound.setEnable(false);
        
        return sound;
    }
    
    public static BackgroundSound createBackgroundSound(String name) {
        URL soundURL = locateSound(name);
        if (soundURL == null) {
            System.err.println("Error: Background sound file not found: " + name);
            return null;
        }
        
        MediaContainer bgMedia = new MediaContainer(soundURL);
        bgMedia.setCacheEnable(true);
        
        BackgroundSound bgSound = new BackgroundSound();
        bgSound.setSoundData(bgMedia);
        bgSound.setInitialGain(5.0f);  // Adjust volume as needed
        bgSound.setLoop(BackgroundSound.INFINITE_LOOPS);  // Loop continuously
        bgSound.setEnable(true);
        bgSound.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000));
        
        return bgSound;
    }
    
    public static void enableAudio(SimpleUniverse simple_U) {
        Viewer viewer = simple_U.getViewer();
        viewer.getView().setBackClipDistance(20.0f);
        
        // Create a BranchGroup to hold all sounds
        BranchGroup soundGroup = new BranchGroup();
        soundGroup.addChild(horrorBGSound);
        soundGroup.addChild(doorBGSound);
        soundGroup.addChild(correctPinBGSound);
        soundGroup.addChild(wrongPinBGSound);
        soundGroup.addChild(heartbeatBGSound);
        soundGroup.addChild(crossRotationSound);
        
        simple_U.getLocale().addBranchGraph(soundGroup);
    }
    
    public static void playBackgroundSound(BackgroundSound sound) {
        if (sound != null) {
            sound.setEnable(true);
        }
    }

    public static void pauseBackgroundSound(BackgroundSound sound) {
        if (sound != null) {
            sound.setEnable(false);
        }
    }

    public static void stopBackgroundSound(BackgroundSound sound) {
        if (sound != null) {
            sound.setEnable(false);
            sound.setSoundData(null);  // Release the sound data
        }
    }
}