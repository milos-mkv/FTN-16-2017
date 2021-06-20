package gfx;

import org.joml.Vector3f;

public class PointLight extends Light {

    public Vector3f position;

    public float constant;
    public float linear;
    public float quadratic;

    public PointLight(Vector3f ambient, Vector3f diffuse, Vector3f specular, Vector3f position,
                      float constant, float linear, float quadratic) {
        super(ambient, diffuse, specular);
        this.position = position;
        this.constant = constant;
        this.linear = linear;
        this.quadratic = quadratic;
    }
}
