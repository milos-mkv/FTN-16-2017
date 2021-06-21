package gfx;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Vertex implements Cloneable {

    public Vector3f position;
    public Vector3f normal;
    public Vector2f texCoords;

    public Vertex() {
        this.position  = new Vector3f(0, 0, 0);
        this.normal    = new Vector3f(0, 0, 0);
        this.texCoords = new Vector2f(0, 0);
    }

    public Vertex clone() throws CloneNotSupportedException {
        Vertex clone    = (Vertex) super.clone();
        clone.position  = (Vector3f) position.clone();
        clone.normal    = (Vector3f) normal.clone();
        clone.texCoords = (Vector2f) texCoords.clone();
        return clone;
    }

}
