package gfx;

import org.joml.Vector3f;

public class DirectionalLight extends Light {

    public Vector3f direction;

    public DirectionalLight(Vector3f ambient, Vector3f diffuse, Vector3f specular, Vector3f direction) {
        super(ambient, diffuse, specular);
        this.direction = direction;
    }
}
