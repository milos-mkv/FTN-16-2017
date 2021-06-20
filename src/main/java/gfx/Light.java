package gfx;

import org.joml.Vector3f;

public class Light {

    public Vector3f ambient;
    public Vector3f diffuse;
    public Vector3f specular;

    public Light(Vector3f ambient, Vector3f diffuse, Vector3f specular) {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
    }
}
