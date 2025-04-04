import org.jogamp.java3d.*;
import org.jogamp.vecmath.*;
import java.util.Iterator;

public class LampToSphereInterpolator extends TransformInterpolator {
    private final TransformGroup targetTG;
    private final Transform3D startTransform = new Transform3D();
    private final Transform3D endTransform = new Transform3D();
    private final double endScale;
    private final BranchGroup targetBranchGroup;

    public LampToSphereInterpolator(Alpha alpha, TransformGroup targetTG, double endScale, BranchGroup targetBranchGroup) {
        super(alpha, targetTG);
        this.targetTG = targetTG;
        this.endScale = endScale;
        this.targetBranchGroup = targetBranchGroup;

        targetTG.getTransform(startTransform);
        endTransform.setScale(endScale);
        Vector3d startPos = new Vector3d();
        startTransform.get(startPos);
        endTransform.setTranslation(startPos);

        this.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        System.out.println("Interpolator created: startScale=" + startTransform.getScale() + ", endScale=" + endScale);
    }

    @Override
    public void computeTransform(float alphaValue, Transform3D transform) {
        double startScale = startTransform.getScale();
        double scale = startScale + (endScale - startScale) * alphaValue;
        transform.setScale(scale);
        Vector3d startPos = new Vector3d();
        startTransform.get(startPos);
        transform.setTranslation(startPos);
        System.out.println("Computing transform: alpha=" + alphaValue + ", scale=" + scale);
    }

    @Override
    public void processStimulus(Iterator<WakeupCriterion> criteria) {
        Alpha alpha = getAlpha();
        if (alpha == null || alpha.finished()) {
            if (targetTG.numChildren() > 0) {
                BranchGroup currentBG = (BranchGroup) targetTG.getChild(0);
                currentBG.detach();
            }
            targetTG.addChild(targetBranchGroup);
            targetTG.setTransform(endTransform);
            this.setEnable(false);
            System.out.println("Animation finished: final scale=" + endScale);
        } else {
            Transform3D currentTransform = new Transform3D();
            computeTransform(alpha.value(), currentTransform);
            targetTG.setTransform(currentTransform);
            wakeupOn(new WakeupOnElapsedFrames(0));
        }
    }
}