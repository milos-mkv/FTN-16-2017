package gfx;

import org.joml.Vector4f;

public class Material {

    public static final Vector4f DEFAULT_COLOR = new Vector4f(1, 1, 1, 1);

    public Vector4f ambientColor;
    public Vector4f diffuseColor;
    public Vector4f specularColor;

    public float shininess;
    public float reflectance;

    public Texture diffuseTexture;
    public Texture specularTexture;
    public Texture normalTexture;

    public Material() {
        this.ambientColor = DEFAULT_COLOR;
        this.diffuseColor = DEFAULT_COLOR;
        this.specularColor = DEFAULT_COLOR;
    }

    public Material(Vector4f ambient, Vector4f diffuse, Vector4f specular, float shininess ) {
        this.ambientColor = ambient;
        this.diffuseColor = diffuse;
        this.specularColor = specular;
        this.shininess = shininess;
    }
}
