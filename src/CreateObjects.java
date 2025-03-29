import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.Primitive;
import org.jogamp.vecmath.AxisAngle4d;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.vecmath.*;

public class CreateObjects {
    public TransformGroup createObject(String objName, AxisAngle4d rot, Vector3d pos, double scale) {
        System.out.println("\nCreating object: " + objName);
        
        Transform3D transform = new Transform3D();
        transform.set(rot);
        transform.setScale(scale);
        transform.setTranslation(pos);
        
        TransformGroup objNode = new TransformGroup(transform);
        objNode.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        BranchGroup object = LoadObject.loadObject(objName);
        if (object != null) {
            objNode.addChild(object);
        } else {
            objNode.addChild(createErrorObject());
        }
        
        return objNode;
    }
    
    private BranchGroup createErrorObject() {
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