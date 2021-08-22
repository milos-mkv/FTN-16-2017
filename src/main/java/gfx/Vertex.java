package gfx;

import lombok.Data;
import org.joml.Vector2f;
import org.joml.Vector3f;

@Data
public class Vertex {

    private Vector3f position;
    private Vector3f normal;
    private Vector2f texCoords;

    public Vertex() {
        this.position  = new Vector3f(0, 0, 0);
        this.normal    = new Vector3f(0, 0, 0);
        this.texCoords = new Vector2f(0, 0);
    }

}
