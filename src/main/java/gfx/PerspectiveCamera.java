package gfx;

import core.Constants;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@NoArgsConstructor
@Data
public class PerspectiveCamera {

    protected Vector3f position;
    protected Vector3f direction;
    protected Vector3f up;
    protected Vector3f right;
    protected Vector3f front;
    protected Vector3f tmp;

    protected float near;
    protected float far;
    protected float fov;
    protected float aspect;

    public PerspectiveCamera(float fov, float aspect, float near, float far) {
        this.fov        = fov;
        this.aspect     = aspect;
        this.near       = near;
        this.far        = far;
        this.position   = new Vector3f(0, 0, 0);
        this.front      = new Vector3f(0, 0, 0);
        this.direction  = new Vector3f(0, 0, 0);
        this.up         = new Vector3f(0, 0, 0);
        this.right      = new Vector3f(0, 0, 0);
        this.tmp        = new Vector3f(0, 0, 0);
        updateCamera();
    }

    public void updateCamera() {
        tmp.set(position).add(front).normalize(direction);
        tmp.set(Constants.WORLD_UP).cross(direction).normalize(right);
        up.set(tmp.set(direction).cross(right));
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(position, direction, up);
    }

    public Matrix4f getProjectionMatrix() {
        return new Matrix4f().perspective(fov, aspect, near, far);
    }
}
