import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.vecmath.*;

public class CreateObjects {
    public TransformGroup createObject(String objName, AxisAngle4d rot, Vector3d pos, double scale) {
        System.out.println("Loading: " + objName);
        
        // Configure transformation
        Transform3D transform = new Transform3D();
        transform.set(rot);
        transform.setScale(scale);
        transform.setTranslation(pos);
        
        // Create node
        TransformGroup objNode = new TransformGroup(transform);
        objNode.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        // Load and attach 3D object
        BranchGroup object = LoadObject.loadObject(objName);
        if (object != null) {
            objNode.addChild(object);
        } else {
            System.err.println("Using fallback object");
            objNode.addChild(createErrorObject());
        }
        
        return objNode;
    }
    
    private BranchGroup createErrorObject() {
        Appearance app = new Appearance();
        app.setMaterial(new Material());
        BranchGroup bg = new BranchGroup();
        bg.addChild(new Box(0.1f, 0.1f, 0.1f, app));
        return bg;
    }
}