import java.net.URL;
import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.java3d.utils.universe.Viewer;
import org.jogamp.vecmath.Point3d;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;

public class SoundEffects {
    // Sound file extensions - modify according to your actual file types
    private static final String SOUND_EXTENSION = ".wav";
    
    // Sound instances with proper initialization
    public static BackgroundSound horrorBGSound;
    public static PointSound doorBGSound;
    public static PointSound correctPinBGSound;
    public static PointSound wrongPinBGSound;
    public static BackgroundSound heartbeatBGSound;
    public static PointSound crossRotationSound;
    
    static {
        // Initialize all sounds
        horrorBGSound = createBackgroundSound("Horror_Ambiance");
        doorBGSound = createPointSound("Door_Open");
        correctPinBGSound = createPointSound("Success_Sound");
        wrongPinBGSound = createPointSound("Error_Sound");
        heartbeatBGSound = createBackgroundSound("Heartbeat");
        crossRotationSound = createPointSound("Success_Sound");
    }
    
    public static void playSound(BackgroundSound sound) {
        if (sound == null) {
            System.err.println("Cannot play null background sound");
            return;
        }
        
        sound.setEnable(true);
        Timer timer = new Timer(7000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sound.setEnable(false);
                ((Timer) e.getSource()).stop();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    public static URL locateSound(String baseFilename) {
        // Try with and without extension for compatibility
        String[] filenameVariants = {
            baseFilename + SOUND_EXTENSION,
            baseFilename,
            "sounds/" + baseFilename + SOUND_EXTENSION,
            "sounds/" + baseFilename
        };
        
        for (String filename : filenameVariants) {
            // Try classpath first
            URL url = SoundEffects.class.getResource("/" + filename);
            if (url != null) {
                System.out.println("Found sound at: " + url);
                return url;
            }
            
            // Try filesystem
            try {
                File file = new File(filename);
                if (file.exists()) {
                    System.out.println("Found sound at: " + file.getAbsolutePath());
                    return file.toURI().toURL();
                }
            } catch (Exception e) {
                System.err.println("Error checking file: " + filename + " - " + e.getMessage());
            }
        }
        
        System.err.println("Sound file not found in any location for: " + baseFilename);
        return null;
    }
    
    public static PointSound createPointSound(String name) {
        URL soundURL = locateSound(name);
        if (soundURL == null) {
            System.err.println("Failed to create PointSound: " + name);
            return null;
        }
        
        try {
            MediaContainer media = new MediaContainer(soundURL);
            media.setCacheEnable(true);
            
            PointSound sound = new PointSound();
            sound.setSoundData(media);
            sound.setInitialGain(1.0f);
            sound.setCapability(PointSound.ALLOW_ENABLE_WRITE);
            sound.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000));
            sound.setEnable(false);
            
            return sound;
        } catch (Exception e) {
            System.err.println("Error creating PointSound: " + e.getMessage());
            return null;
        }
    }
    
    public static BackgroundSound createBackgroundSound(String name) {
        URL soundURL = locateSound(name);
        if (soundURL == null) {
            System.err.println("Failed to create BackgroundSound: " + name);
            return null;
        }
        
        try {
            MediaContainer bgMedia = new MediaContainer(soundURL);
            bgMedia.setCacheEnable(true);
            
            BackgroundSound bgSound = new BackgroundSound();
            bgSound.setSoundData(bgMedia);
            bgSound.setInitialGain(0.5f);  // Adjust volume as needed
            bgSound.setLoop(BackgroundSound.INFINITE_LOOPS);
            bgSound.setEnable(false);  // Start disabled by default
            bgSound.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000));
            bgSound.setCapability(BackgroundSound.ALLOW_ENABLE_WRITE);
            
            return bgSound;
        } catch (Exception e) {
            System.err.println("Error creating BackgroundSound: " + e.getMessage());
            return null;
        }
    }
    
    public static void enableAudio(SimpleUniverse simple_U) {
        if (simple_U == null) {
            System.err.println("Cannot enable audio - null universe");
            return;
        }
        
        Viewer viewer = simple_U.getViewer();
        if (viewer != null) {
            viewer.getView().setBackClipDistance(20.0f);
        }
        
        // Create a BranchGroup to hold all sounds
        BranchGroup soundGroup = new BranchGroup();
        soundGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        
        // Add all sounds that were successfully created
        if (horrorBGSound != null) soundGroup.addChild(horrorBGSound);
        if (doorBGSound != null) soundGroup.addChild(doorBGSound);
        if (correctPinBGSound != null) soundGroup.addChild(correctPinBGSound);
        if (wrongPinBGSound != null) soundGroup.addChild(wrongPinBGSound);
        if (heartbeatBGSound != null) soundGroup.addChild(heartbeatBGSound);
        if (crossRotationSound != null) soundGroup.addChild(crossRotationSound);
        
        simple_U.getLocale().addBranchGraph(soundGroup);
    }
    
    public static void playSound(PointSound sound) {
        if (sound != null) {
            sound.setEnable(true);
            new Thread(() -> {
                try {
                    Thread.sleep(2000); // Auto-stop after 2 seconds
                    sound.setEnable(false);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Sound thread interrupted");
                }
            }).start();
        } else {
            System.err.println("Cannot play null PointSound");
        }
    }
    
    public static void playBackgroundSound(BackgroundSound sound) {
        if (sound != null) {
            sound.setEnable(true);
        } else {
            System.err.println("Cannot play null BackgroundSound");
        }
    }

    public static void pauseBackgroundSound(BackgroundSound sound) {
        if (sound != null) {
            sound.setEnable(false);
        } else {
            System.err.println("Cannot pause null BackgroundSound");
        }
    }

    public static void stopBackgroundSound(BackgroundSound sound) {
        if (sound != null) {
            sound.setEnable(false);
            sound.setSoundData(null);  // Release the sound data
        } else {
            System.err.println("Cannot stop null BackgroundSound");
        }
    }
}