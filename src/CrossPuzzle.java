// CrossPuzzle.java
import org.jogamp.java3d.*;
import org.jogamp.vecmath.*;
import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class CrossPuzzle {
    private TransformGroup crossMiddle;
    private int rotationCount = 0;
    private ArrayList<TransformGroup> connectedCrosses;
    
    public CrossPuzzle(TransformGroup crossMiddle) {
        this.crossMiddle = crossMiddle;
        this.connectedCrosses = new ArrayList<TransformGroup>();
        setupBehavior();
    }
    
    public void addConnectedCross(TransformGroup cross) {
        connectedCrosses.add(cross);
    }
    
    private void setupBehavior() {
        Behavior clickBehavior = new Behavior() {
            private WakeupOnAWTEvent wakeCondition = new WakeupOnAWTEvent(MouseEvent.MOUSE_CLICKED);
            
            @Override
            public void initialize() {
                wakeupOn(wakeCondition);
            }
            
            @Override
            public void processStimulus(Iterator<WakeupCriterion> criteria) {
                while (criteria.hasNext()) {
                    WakeupCriterion criterion = criteria.next();
                    if (criterion instanceof WakeupOnAWTEvent) {
                        AWTEvent[] events = ((WakeupOnAWTEvent)criterion).getAWTEvent();
                        for (AWTEvent event : events) {
                            if (event instanceof MouseEvent) {
                                MouseEvent me = (MouseEvent) event;
                                if (me.getButton() == MouseEvent.BUTTON1) {
                                    rotateCross();
                                }
                            }
                        }
                    }
                }
                wakeupOn(wakeCondition);
            }
        };
        
        BoundingSphere bounds = new BoundingSphere(new Point3d(0, 0, 0), 100.0);
        clickBehavior.setSchedulingBounds(bounds);
        crossMiddle.addChild(clickBehavior);
    }
    
    private void rotateCross() {
        Transform3D rotation = new Transform3D();
        rotation.rotX(Math.toRadians(90));
        
        Transform3D currentTransform = new Transform3D();
        crossMiddle.getTransform(currentTransform);
        currentTransform.mul(rotation);
        crossMiddle.setTransform(currentTransform);
        
        rotationCount++;
        
        if (rotationCount % 4 == 0) {
            SoundEffects.playSound(SoundEffects.crossRotationSound);
            
            for (TransformGroup cross : connectedCrosses) {
                cross.getTransform(currentTransform);
                currentTransform.mul(rotation);
                cross.setTransform(currentTransform);
            }
        }
    }
}