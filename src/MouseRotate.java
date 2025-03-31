import org.jogamp.java3d.*;
import org.jogamp.vecmath.*;
import java.awt.AWTEvent;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MouseRotate extends Behavior {
    private WakeupCondition wakeupCondition;
    private TransformGroup transformGroup;
    private Transform3D transform = new Transform3D();
    private Matrix4d matrix = new Matrix4d();
    
    private double xFactor = 0.02;
    private double yFactor = 0.02;
    
    private int x_last, y_last;
    private int x_current, y_current;
    private boolean first_time = true;
    
    // Interface for listeners
    public interface Listener {
        void transformChanged(Transform3D transform);
    }
    
    // List to hold listeners
    private List<Listener> listeners = new ArrayList<>();
    
    public MouseRotate() {
        this(0, 0);
    }
    
    public MouseRotate(int xbase, int ybase) {
        x_last = xbase;
        y_last = ybase;
    }
    
    public void initialize() {
        wakeupCondition = new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED);
        wakeupOn(wakeupCondition);
    }
    
    public void processStimulus(Enumeration criteria) {
        WakeupOnAWTEvent ev = (WakeupOnAWTEvent)criteria.nextElement();
        AWTEvent[] events = ev.getAWTEvent();
        
        if (transformGroup == null) {
            wakeupOn(wakeupCondition);
            return;
        }
        
        for (int i = 0; i