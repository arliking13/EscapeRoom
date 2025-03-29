import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.jogamp.java3d.*;
import org.jogamp.java3d.loaders.*;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.vecmath.*;

public class LoadObject {
    private static final Map<String, String> OBJECT_TEXTURES = new HashMap<>();
    
    static {
        // Default texture assignments
        OBJECT_TEXTURES.put("room3", "rust_walls.jpg");
        OBJECT_TEXTURES.put("ChairOld", "wood_planks.jpg");
        OBJECT_TEXTURES.put("Desk", "wood.jpg");
        OBJECT_TEXTURES.put("Locker", "metal.jpg");
        OBJECT_TEXTURES.put("Door", "wood_door.jpg");
        OBJECT_TEXTURES.put("Escape_door", "metal_door.jpg");
        OBJECT_TEXTURES.put("Window_Casement_Frame", "window_frame.jpg");
        OBJECT_TEXTURES.put("Baseboard", "wood_trim.jpg");
        OBJECT_TEXTURES.put("Wall_light_left", "light_fixture.jpg");
        OBJECT_TEXTURES.put("Wall_light_right", "light_fixture.jpg");
        OBJECT_TEXTURES.put("Calling_lamp", "lamp.jpg");
        OBJECT_TEXTURES.put("Paper", "paper.jpg");
        OBJECT_TEXTURES.put("SwitchMain", "switch.jpg");
        OBJECT_TEXTURES.put("SwitchHandle", "metal.jpg");
        OBJECT_TEXTURES.put("KeypadDoorLock", "keypad.jpg");
        OBJECT_TEXTURES.put("Lockers_door", "metal.jpg");
        OBJECT_TEXTURES.put("Cross_left", "wood_cross.jpg");
        OBJECT_TEXTURES.put("Cross_middle", "wood_cross.jpg");
        OBJECT_TEXTURES.put("Cross_right", "wood_cross.jpg");
        OBJECT_TEXTURES.put("The_leftmost_cross", "wood_cross.jpg");
        OBJECT_TEXTURES.put("The_rightmost_cross", "wood_cross.jpg");
    }
    
    public static void setObjectTexture(String objName, String textureName) {
        OBJECT_TEXTURES.put(objName, textureName);
    }
    
    public static String getObjectTexture(String objName) {
        return OBJECT_TEXTURES.get(objName);
    }
    
    public static boolean hasCustomTexture(String objName) {
        return OBJECT_TEXTURES.containsKey(objName);
    }
    
    public static Appearance obj_Appearance(String fileName, int mtlNum) {
        String objName = fileName.replace("room3/", "").replace(".obj", "");
        
        if (hasCustomTexture(objName)) {
            return createTexturedAppearance(getObjectTexture(objName));
        }
        
        try {
            MTLFile mtl = new MTLFile(fileName, mtlNum);
            return createAppearanceFromMTL(mtl);
        } catch (Exception e) {
            System.err.println("Error loading material: " + e.getMessage());
            return createDefaultAppearance();
        }
    }
    
    private static Appearance createAppearanceFromMTL(MTLFile mtl) {
        Material mat = new Material();
        mat.setAmbientColor(mtl.ambient);
        mat.setDiffuseColor(mtl.diffuse);
        mat.setSpecularColor(mtl.specular);
        mat.setEmissiveColor(mtl.emissive);
        mat.setShininess(mtl.shininess);
        mat.setLightingEnable(true);
        
        Appearance app = new Appearance();
        app.setMaterial(mat);
        
        if (mtl.texture != null) {
            app.setTexture(mtl.texture);
            app.setTextureAttributes(mtl.texAttributes);
            
            TransparencyAttributes ta = new TransparencyAttributes();
            ta.setTransparencyMode(TransparencyAttributes.NONE);
            app.setTransparencyAttributes(ta);
        }
        
        return app;
    }
    
    public static Appearance createTexturedAppearance(String textureName) {
        String[] possiblePaths = {
            "room3/textures/" + textureName,
            "textures/" + textureName,
            textureName
        };
        
        for (String path : possiblePaths) {
            try {
                TextureLoader loader = new TextureLoader(path, null);
                Texture texture = loader.getTexture();
                if (texture != null) {
                    return createStandardAppearance(texture);
                }
            } catch (Exception e) {
                System.err.println("Texture load failed: " + path);
            }
        }
        return createDefaultAppearance();
    }
    
    private static Appearance createStandardAppearance(Texture texture) {
        texture.setBoundaryModeS(Texture.WRAP);
        texture.setBoundaryModeT(Texture.WRAP);
        texture.setMinFilter(Texture.BASE_LEVEL_LINEAR);
        texture.setMagFilter(Texture.BASE_LEVEL_LINEAR);
        
        Material mat = new Material();
        mat.setAmbientColor(0.5f, 0.5f, 0.5f);
        mat.setDiffuseColor(1f, 1f, 1f);
        mat.setSpecularColor(0.2f, 0.2f, 0.2f);
        mat.setShininess(32f);
        mat.setLightingEnable(true);
        
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        
        Appearance app = new Appearance();
        app.setMaterial(mat);
        app.setTexture(texture);
        app.setTextureAttributes(texAttr);
        
        TransparencyAttributes ta = new TransparencyAttributes();
        ta.setTransparencyMode(TransparencyAttributes.NONE);
        app.setTransparencyAttributes(ta);
        
        return app;
    }
    
    private static Appearance createDefaultAppearance() {
        Material mat = new Material();
        mat.setAmbientColor(0.5f, 0.5f, 0.5f);
        mat.setDiffuseColor(0.8f, 0.8f, 0.8f);
        mat.setSpecularColor(0.5f, 0.5f, 0.5f);
        mat.setShininess(32f);
        mat.setLightingEnable(true);
        
        Appearance app = new Appearance();
        app.setMaterial(mat);
        return app;
    }
    
    public static BranchGroup loadObject(String objName) {
        String objPath = "room3/" + objName + ".obj";
        System.out.println("Loading: " + objPath);
        
        if (!new File(objPath).exists()) {
            System.err.println("Error: File not found");
            return createErrorObject();
        }
        
        int flags = ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.LOAD_ALL;
        ObjectFile loader = new ObjectFile(flags, (float)Math.toRadians(60));
        
        try {
            Scene loadedScene = loader.load(objPath);
            if (loadedScene == null || loadedScene.getSceneGroup() == null) {
                return createErrorObject();
            }
            
            BranchGroup objGroup = loadedScene.getSceneGroup();
            applyAppearances(objGroup, objName);
            return objGroup;
        } catch (Exception e) {
            System.err.println("Error loading object: " + e.getMessage());
            return createErrorObject();
        }
    }
    
    private static void applyAppearances(BranchGroup objGroup, String objName) {
        for (int i = 0; i < objGroup.numChildren(); i++) {
            Node child = objGroup.getChild(i);
            if (child instanceof Shape3D) {
                ((Shape3D) child).setAppearance(
                    obj_Appearance("room3/" + objName + ".obj", i));
            }
        }
    }
    
    private static BranchGroup createErrorObject() {
        Appearance app = new Appearance();
        Material mat = new Material();
        mat.setDiffuseColor(new Color3f(1f, 0f, 0f));
        mat.setLightingEnable(true);
        app.setMaterial(mat);
        
        BranchGroup bg = new BranchGroup();
        bg.addChild(new Box(0.2f, 0.2f, 0.2f, app));
        return bg;
    }
}