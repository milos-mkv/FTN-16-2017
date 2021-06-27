package gfx;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TransformComponent implements Cloneable {

    public Vector3f position;
    public Vector3f rotation;
    public Vector3f scale;

    public TransformComponent() {
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
        this.scale    = new Vector3f(1, 1, 1);
    }

    public Matrix4f getTransform() {
        return new Matrix4f().translate(position).rotate(rotation.x, 1, 0, 0)
                .rotate(rotation.y, 0, 1, 0).rotate(rotation.z, 0, 0, 1).scale(scale);
    }

    public TransformComponent clone() throws CloneNotSupportedException {
        TransformComponent clone = (TransformComponent) super.clone();
        clone.position = (Vector3f) position.clone();
        clone.rotation = (Vector3f) rotation.clone();
        clone.scale    = (Vector3f) scale.clone();
        return clone;
    }
}
