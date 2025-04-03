import org.jogamp.java3d.*;
import org.jogamp.vecmath.*;
import java.util.Iterator;

public class LampToSphereInterpolator extends TransformInterpolator {
    private final TransformGroup targetTG;
    private final Transform3D startTransform = new Transform3D();
    private final Transform3D endTransform = new Transform3D();

    public LampToSphereInterpolator(Alpha alpha, TransformGroup targetTG) {
        super(alpha, targetTG); // Pass both Alpha and TransformGroup to the superclass constructor
        this.targetTG = targetTG;

        // Get the initial transform (lamp's current state)
        targetTG.getTransform(startTransform);

        // Define the end transform (scaled to match smaller sphere size)
        endTransform.setScale(0.05); // Matches sphere size
        Vector3d startPos = new Vector3d();
        startTransform.get(startPos);
        endTransform.setTranslation(startPos); // Preserve original position

        // Set scheduling bounds
        this.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        System.out.println("Interpolator created with start scale: " + startTransform.getScale() + ", end scale: 0.05");
    }

    @Override
    public void initialize() {
        // Schedule the first wakeup to start the animation
        System.out.println("Interpolator initialized, starting animation...");
        wakeupOn(new WakeupOnElapsedFrames(0));
    }

    @Override
    public void computeTransform(float alphaValue, Transform3D transform) {
        // Interpolate between start and end transforms
        double startScale = startTransform.getScale();
        double endScale = 0.05;
        double scale = startScale + (endScale - startScale) * alphaValue; // Linear interpolation
        transform.setScale(scale);

        // Preserve the original position
        Vector3d startPos = new Vector3d();
        startTransform.get(startPos);
        transform.setTranslation(startPos);

        System.out.println("Computing transform: alpha = " + alphaValue + ", scale = " + scale);
    }

    @Override
    public void processStimulus(Iterator<WakeupCriterion> criteria) {
        Alpha alpha = getAlpha();
        if (alpha == null || alpha.finished()) {
            // Animation complete, ensure final state
            targetTG.setTransform(endTransform);
            this.setEnable(false); // Stop the interpolator
            System.out.println("Animation finished, final scale set to 0.05");
        } else {
            // Update the transform based on the current alpha value
            Transform3D currentTransform = new Transform3D();
            computeTransform(alpha.value(), currentTransform);
            targetTG.setTransform(currentTransform);
            wakeupOn(new WakeupOnElapsedFrames(0)); // Continue animation
        }
    }
}