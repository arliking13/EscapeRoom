import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.vecmath.*;

public class CreateObjects {
    private CrossPuzzle crossPuzzle;
    
    public TransformGroup createObject(String objName, AxisAngle4d rot, Vector3d pos, double scale) {
        System.out.println("\nCreating object: " + objName);
        
        Transform3D transform = new Transform3D();
        transform.set(rot);
        transform.setScale(scale);
        transform.setTranslation(pos);
        
        TransformGroup objNode = new TransformGroup(transform);
        objNode.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objNode.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        objNode.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        
        BranchGroup object = LoadObject.loadObject(objName);
        if (object != null) {
            objNode.addChild(object);
        } else {
            objNode.addChild(createErrorObject());
        }

        // Set up puzzle behavior for crosses
        if (objName.equals("Cross_middle")) {
            crossPuzzle = new CrossPuzzle(objNode);
        }
        
        // Connect other crosses to the puzzle
        if (crossPuzzle != null && isConnectedCross(objName)) {
            crossPuzzle.addConnectedCross(objNode);
        }
        
        return objNode;
    }
    
    private boolean isConnectedCross(String objName) {
        return objName.equals("Cross_left") || 
               objName.equals("Cross_right") ||
               objName.equals("The_leftmost_cross") ||
               objName.equals("The_rightmost_cross");
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