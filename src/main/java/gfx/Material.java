package gfx;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Material {

    public static final Vector4f DEFAULT_COLOR = new Vector4f(1, 1, 1, 1);

    public Vector4f ambientColor;
    public Vector4f diffuseColor;
    public Vector4f specularColor;

    public float shniness;
    public float reflectance;

    public Texture texture;
    public Texture normalMap;

    public Material() {
        this.ambientColor = DEFAULT_COLOR;
        this.diffuseColor = DEFAULT_COLOR;
        this.specularColor = DEFAULT_COLOR;
        this.texture = null;
        this.normalMap = null;
        this.reflectance = 0;
    }
}
