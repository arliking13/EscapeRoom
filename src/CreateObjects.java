import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.geometry.Sphere;
import org.jogamp.vecmath.*;
import java.util.Iterator;

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
        objNode.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        
        BranchGroup object = LoadObject.loadObject(objName);
        if (object != null) {
            object.setCapability(BranchGroup.ALLOW_DETACH);
            
            if (objName.equals("Ceiling_lamp")) {
                setupPickability(object);
            }
            objNode.addChild(object);
        } else {
            objNode.addChild(createErrorObject());
        }

        if (objName.equals("Cross_middle")) {
            crossPuzzle = new CrossPuzzle(objNode);
        }
        
        if (crossPuzzle != null && isConnectedCross(objName)) {
            crossPuzzle.addConnectedCross(objNode);
        }
        
        return objNode;
    }
    
    private void setupPickability(BranchGroup object) {
        Iterator<Node> children = object.getAllChildren();
        while (children.hasNext()) {
            Node child = children.next();
            if (child instanceof Shape3D) {
                Shape3D shape = (Shape3D)child;
                shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
                shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
                shape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
                shape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
                shape.setPickable(true);
            }
        }
    }
    
    public static void transformToSphere(TransformGroup objNode) {
        for (int i = 0; i < objNode.numChildren(); i++) {
            Node child = objNode.getChild(i);
            if (child instanceof BranchGroup) {
                BranchGroup bg = (BranchGroup)child;
                bg.detach();
            }
        }
        
        objNode.removeAllChildren();
        
        Appearance app = new Appearance();
        Material mat = new Material();
        mat.setDiffuseColor(new Color3f(1f, 0f, 0f));
        mat.setLightingEnable(true);
        app.setMaterial(mat);
        
        BranchGroup sphereGroup = new BranchGroup();
        sphereGroup.setCapability(BranchGroup.ALLOW_DETACH);
        
        Sphere sphere = new Sphere(0.8f, app); // Larger sphere size
        sphereGroup.addChild(sphere);
        
        objNode.addChild(sphereGroup);
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