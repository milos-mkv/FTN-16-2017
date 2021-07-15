package gfx;

import lombok.Data;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * The Transform component determines the Position, Rotation, and Scale of each object in the scene.
 */
@Data
public class TransformComponent {

    protected Vector3f position;
    protected Vector3f rotation;
    protected Vector3f scale;
    protected float rotationAngle;

    public TransformComponent() {
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
        this.scale    = new Vector3f(1, 1, 1);
        this.rotationAngle = 0;
    }

    /**
     * This function calculates and returns the transform matrix.
     * @return Matrix4f
     */
    public Matrix4f getTransform() {
        return new Matrix4f().translate(position).rotate(rotationAngle, rotation).scale(scale);
    }

}
