// SoundEffects.java
import java.nio.ByteBuffer;
import java.util.HashMap;
import com.jogamp.openal.*;
import com.jogamp.openal.util.ALut;

public class SoundEffects {
<<<<<<< HEAD
    // Sound file extensions - modify according to your actual file types
    private static final String SOUND_EXTENSION = ".wav";
    
    // SoundUtilityJOAL instance
    private static SoundUtilityJOAL soundUtility = new SoundUtilityJOAL();
    
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
        
        // Load sounds in SoundUtilityJOAL
        soundUtility.load("Door_Open", false);
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
            "sounds/" + baseFilename,
            "./sounds/" + baseFilename + SOUND_EXTENSION,
            "C:/Users/vansh/git/EscapeRoom/sounds/" + baseFilename + SOUND_EXTENSION
        };
        
        System.out.println("Current working directory: " + new java.io.File(".").getAbsolutePath());
        
        for (String filename : filenameVariants) {
            // Try classpath first
            URL url = SoundEffects.class.getResource("C:/Users/vansh/git/EscapeRoom/sounds" + filename);
            System.out.println("Checking classpath for: /" + filename);
            if (url != null) {
                System.out.println("Found sound at: " + url);
                return url;
            }
            
            // Try filesystem
            try {
                java.io.File file = new java.io.File(filename);
                System.out.println("Checking filesystem for: " + file.getAbsolutePath());
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
=======
    private static final String SOUND_DIR = "sounds/";
    private static SoundEffects instance;
    private AL al;
    private HashMap<String, int[]> buffersMap = new HashMap<>();
    private HashMap<String, int[]> sourcesMap = new HashMap<>();

    public static void load(String name, boolean loop) {
        if (get().loadInternal(name, loop)) {
            System.out.println("✅ Sound loaded: " + name);
>>>>>>> branch 'main' of https://github.com/arliking13/EscapeRoom
        } else {
            System.out.println("❌ Failed to load sound: " + name);
        }
    }

    public static void load(String name, float x, float y, float z, boolean loop) {
        if (get().loadInternal(name, loop)) {
            get().setPos(name, x, y, z);
            System.out.println("✅ Loaded + positioned: " + name + " at (" + x + ", " + y + ", " + z + ")");
        } else {
            System.out.println("❌ Failed to load positional sound: " + name);
        }
    }

    public static void play(String name) {
        get().playInternal(name);
    }

    public static void cleanup() {
        if (instance != null) {
            instance.cleanUp();
            System.out.println("🧹 Sound system cleaned up.");
        }
    }
<<<<<<< HEAD
    
    // New method to play sound using SoundUtilityJOAL
    public static void playJOALSound(String soundName) {
        soundUtility.play(soundName);
    }
}
=======

    private static SoundEffects get() {
        if (instance == null) {
            instance = new SoundEffects();
        }
        return instance;
    }

    private SoundEffects() {
        try {
            ALut.alutInit();
            al = ALFactory.getAL();
            al.alGetError();
            initListener();
            System.out.println("🔊 OpenAL initialized.");
        } catch (ALException e) {
            System.err.println("❌ Failed to initialize OpenAL:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void initListener() {
        al.alListener3f(ALConstants.AL_POSITION, 0, 0, 0);
        al.alListener3i(ALConstants.AL_VELOCITY, 0, 0, 0);
        float[] orientation = {0, 0, -1, 0, 1, 0};
        al.alListenerfv(ALConstants.AL_ORIENTATION, orientation, 0);
    }

    private boolean loadInternal(String name, boolean loop) {
        if (sourcesMap.containsKey(name)) return true;

        ByteBuffer[] data = new ByteBuffer[1];
        int[] format = new int[1], size = new int[1], freq = new int[1], loopFlag = new int[1];

        try {
            ALut.alutLoadWAVFile(SOUND_DIR + name + ".wav", format, data, size, freq, loopFlag);
        } catch (ALException e) {
            System.err.println("❌ Error loading WAV file: " + name);
            return false;
        }

        int[] buffer = new int[1];
        al.alGenBuffers(1, buffer, 0);
        al.alBufferData(buffer[0], format[0], data[0], size[0], freq[0]);

        int[] source = new int[1];
        al.alGenSources(1, source, 0);
        al.alSourcei(source[0], ALConstants.AL_BUFFER, buffer[0]);
        al.alSourcef(source[0], ALConstants.AL_GAIN, 1.0f);
        al.alSource3f(source[0], ALConstants.AL_POSITION, 0, 0, 0);
        al.alSourcei(source[0], ALConstants.AL_LOOPING, loop ? ALConstants.AL_TRUE : ALConstants.AL_FALSE);

        buffersMap.put(name, buffer);
        sourcesMap.put(name, source);
        return true;
    }

    private void setPos(String name, float x, float y, float z) {
        int[] source = sourcesMap.get(name);
        if (source != null) {
            al.alSource3f(source[0], ALConstants.AL_POSITION, x, y, z);
        }
    }

    private void playInternal(String name) {
        int[] source = sourcesMap.get(name);
        if (source != null) {
            System.out.println("🔊 Playing sound: " + name);
            al.alSourcePlay(source[0]);
        } else {
            System.out.println("⚠️ Sound not loaded: " + name);
        }
    }

    private void cleanUp() {
        for (String name : sourcesMap.keySet()) {
            al.alSourceStop(sourcesMap.get(name)[0]);
            al.alDeleteSources(1, sourcesMap.get(name), 0);
            al.alDeleteBuffers(1, buffersMap.get(name), 0);
        }
        ALut.alutExit();
    }
}
>>>>>>> branch 'main' of https://github.com/arliking13/EscapeRoom
