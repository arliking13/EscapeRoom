import java.io.*;
import java.util.*;
import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.vecmath.Color3f;

public class MTLFile {
    public float shininess = 32f;
    public Color3f ambient = new Color3f(0.2f, 0.2f, 0.2f);
    public Color3f diffuse = new Color3f(0.8f, 0.8f, 0.8f);
    public Color3f specular = new Color3f(0.5f, 0.5f, 0.5f);
    public Color3f emissive = new Color3f(0f, 0f, 0f);
    public Texture texture = null;
    
    public MTLFile(String objPath, int mtlNum) {
        File mtlFile = new File(objPath.replace(".obj", ".mtl"));
        if (!mtlFile.exists()) return;
        
        try (Scanner sc = new Scanner(mtlFile)) {
            String targetMaterial = findTargetMaterial(sc, objPath, mtlNum);
            if (targetMaterial != null) {
                parseMaterial(sc, targetMaterial);
            }
        } catch (Exception e) {
            System.err.println("MTL parsing error: " + e.getMessage());
        }
    }
    
    private String findTargetMaterial(Scanner sc, String objPath, int mtlNum) throws IOException {
        try (Scanner objScanner = new Scanner(new File(objPath))) {
            while (objScanner.hasNextLine()) {
                String line = objScanner.nextLine().trim();
                if (line.startsWith("usemtl") && mtlNum-- == 0) {
                    return line.split("\\s+")[1];
                }
            }
        }
        return null;
    }
    
    private void parseMaterial(Scanner sc, String targetMaterial) {
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.startsWith("newmtl " + targetMaterial)) {
                readMaterialProperties(sc);
                break;
            }
        }
    }
    
    private void readMaterialProperties(Scanner sc) {
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            if (line.startsWith("newmtl")) break;
            
            String[] parts = line.split("\\s+");
            try {
                switch (parts[0]) {
                    case "Ns": shininess = Float.parseFloat(parts[1]); break;
                    case "Ka": ambient = parseColor(parts); break;
                    case "Kd": diffuse = parseColor(parts); break;
                    case "Ks": specular = parseColor(parts); break;
                    case "Ke": emissive = parseColor(parts); break;
                    case "map_Kd": 
                        System.out.println("Note: Texture loading disabled - using material colors");
                        break;
                }
            } catch (Exception e) {
                System.err.println("Skipping bad MTL line: " + line);
            }
        }
    }
    
    private Color3f parseColor(String[] parts) {
        return new Color3f(
            Float.parseFloat(parts[1]),
            Float.parseFloat(parts[2]),
            Float.parseFloat(parts[3]));
    }
}