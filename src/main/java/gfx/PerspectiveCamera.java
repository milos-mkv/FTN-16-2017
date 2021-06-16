package gfx;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class PerspectiveCamera {

    public static final Vector3f WORLD_UP = new Vector3f(0, 1, 0);

    public Vector3f position;
    public Vector3f direction;
    public Vector3f up;
    public Vector3f right;
    public Vector3f front;
    public Vector3f tmp = new Vector3f();

    public float near;
    public float far;
    public float fov;
    public float aspect;

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
        UpdateCamera();
    }

    public void UpdateCamera() {
        tmp.set(position).add(front).normalize(direction);
        tmp.set(WORLD_UP).cross(direction).normalize(right);
        up.set(tmp.set(direction).cross(right));
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(position, direction, up);
    }

    public Matrix4f getProjectionMatrix() {
        return new Matrix4f().perspective(fov, aspect, near, far);
    }

}
