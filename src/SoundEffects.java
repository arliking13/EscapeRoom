// SoundEffects.java
import java.nio.ByteBuffer;
import java.util.HashMap;
import com.jogamp.openal.*;
import com.jogamp.openal.util.ALut;

public class SoundEffects {
    private static final String SOUND_DIR = "sounds/";
    private static SoundEffects instance;
    private AL al;
    private HashMap<String, int[]> buffersMap = new HashMap<>();
    private HashMap<String, int[]> sourcesMap = new HashMap<>();

    public static void load(String name, boolean loop) {
        if (get().loadInternal(name, loop)) {
            System.out.println("✅ Sound loaded: " + name);
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
    public static void setGain(String name, float gain) {
        int[] source = get().sourcesMap.get(name);
        if (source != null) {
            get().al.alSourcef(source[0], ALConstants.AL_GAIN, gain);
        }
    }

}
