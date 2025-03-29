import java.io.*;
import java.util.*;
import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.vecmath.*;

public class MTLFile {
    public float shininess = 32f;
    public Color3f ambient = new Color3f(0.2f, 0.2f, 0.2f);
    public Color3f diffuse = new Color3f(0.8f, 0.8f, 0.8f);
    public Color3f specular = new Color3f(0.5f, 0.5f, 0.5f);
    public Color3f emissive = new Color3f(0f, 0f, 0f);
    public Texture texture = null;
    public TextureAttributes texAttributes = new TextureAttributes();
    
    public MTLFile(String objPath, int mtlNum) throws IOException {
        File mtlFile = new File(objPath.replace(".obj", ".mtl"));
        if (!mtlFile.exists()) {
            String objName = new File(objPath).getName().replace(".obj", "");
            if (LoadObject.hasCustomTexture(objName)) {
                loadTexture(LoadObject.getObjectTexture(objName), objPath);
            }
            return;
        }
        
        try (Scanner sc = new Scanner(mtlFile)) {
            String targetMaterial = findTargetMaterial(sc, objPath, mtlNum);
            if (targetMaterial != null) {
                parseMaterial(sc, targetMaterial, objPath);
            }
        }
        
        texAttributes.setTextureMode(TextureAttributes.MODULATE);
    }
    
    private String findTargetMaterial(Scanner sc, String objPath, int mtlNum) throws IOException {
        try (Scanner objScanner = new Scanner(new File(objPath))) {
            int currentMtl = -1;
            while (objScanner.hasNextLine()) {
                String line = objScanner.nextLine().trim();
                if (line.startsWith("usemtl")) {
                    currentMtl++;
                    if (currentMtl == mtlNum) {
                        return line.split("\\s+")[1];
                    }
                }
            }
        }
        return null;
    }
    
    private void parseMaterial(Scanner sc, String targetMaterial, String objPath) {
        boolean foundMaterial = false;
        String objName = new File(objPath).getName().replace(".obj", "");
        
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.startsWith("newmtl")) {
                if (foundMaterial) break;
                foundMaterial = line.substring(7).equals(targetMaterial);
                continue;
            }
            
            if (!foundMaterial || line.isEmpty()) continue;
            
            String[] parts = line.split("\\s+");
            try {
                switch (parts[0]) {
                    case "Ns": shininess = Float.parseFloat(parts[1]); break;
                    case "Ka": ambient = parseColor(parts); break;
                    case "Kd": diffuse = parseColor(parts); break;
                    case "Ks": specular = parseColor(parts); break;
                    case "Ke": emissive = parseColor(parts); break;
                    case "map_Kd": 
                        if (LoadObject.hasCustomTexture(objName)) {
                            loadTexture(LoadObject.getObjectTexture(objName), objPath);
                        } else {
                            loadTexture(parts[1], objPath);
                        }
                        break;
                }
            } catch (Exception e) {
                System.err.println("Skipping bad MTL line: " + line);
            }
        }
    }
    
    private void loadTexture(String texName, String objPath) {
        texName = texName.replace("\\", "/");
        File objFile = new File(objPath);
        String basePath = objFile.getParent();
        
        String[] possiblePaths = {
            basePath + "/textures/" + texName,
            basePath + "/" + texName,
            "room3/textures/" + texName,
            "textures/" + texName,
            texName
        };
        
        for (String path : possiblePaths) {
            try {
                System.out.println("Trying texture: " + path);
                TextureLoader loader = new TextureLoader(path, null);
                texture = loader.getTexture();
                if (texture != null) {
                    texture.setBoundaryModeS(Texture.WRAP);
                    texture.setBoundaryModeT(Texture.WRAP);
                    texture.setMinFilter(Texture.BASE_LEVEL_LINEAR);
                    texture.setMagFilter(Texture.BASE_LEVEL_LINEAR);
                    System.out.println("Loaded texture from: " + path);
                    return;
                }
            } catch (Exception e) {
                System.err.println("Texture load failed: " + e.getMessage());
            }
        }
        System.err.println("All texture paths failed for: " + texName);
    }
    
    private Color3f parseColor(String[] parts) {
        return new Color3f(
            Float.parseFloat(parts[1]),
            Float.parseFloat(parts[2]),
            Float.parseFloat(parts[3]));
    }
}