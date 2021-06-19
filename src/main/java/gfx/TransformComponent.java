package gfx;

import org.joml.Vector3f;

public class TransformComponent {

    public Vector3f position;
    public Vector3f rotation;
    public Vector3f scale;

    public TransformComponent() {
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
        this.scale    = new Vector3f(1, 1, 1);
    }
}
