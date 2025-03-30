import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.loaders.Scene;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;

import java.io.FileReader;
import java.io.FileNotFoundException;

public class Cross3D {
    private TransformGroup crossTG; // TransformGroup for the cross
    private Transform3D rotation = new Transform3D();
    private float angle = 0.0f;

    public Cross3D(String objPath) {
        crossTG = new TransformGroup();
        crossTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        // Load OBJ model and attach to TransformGroup
        BranchGroup crossModel = loadOBJ(objPath);
        if (crossModel != null) {
            crossTG.addChild(crossModel);
        }
    }

    // Loads an .obj file and returns a BranchGroup containing the 3D model
    private BranchGroup loadOBJ(String filePath) {
        BranchGroup objRoot = new BranchGroup();
        ObjectFile loader = new ObjectFile(ObjectFile.RESIZE);
        Scene scene = null;

        try {
            scene = loader.load(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            System.out.println("Error: OBJ file not found!");
            return null;
        }

        objRoot.addChild(scene.getSceneGroup());
        return objRoot;
    }

    public TransformGroup getTransformGroup() {
        return crossTG;
    }

    // Rotate the cross by 45 degrees on each call
    public void rotate() {
        angle += Math.PI / 4; // Rotate by 45 degrees
        rotation.rotY(angle);
        crossTG.setTransform(rotation);
    }
}

