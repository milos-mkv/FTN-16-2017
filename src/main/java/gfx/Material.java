package gfx;

import core.Constants;
import org.joml.Vector4f;

public class Material implements Cloneable {

    public Vector4f ambientColor;
    public Vector4f diffuseColor;
    public Vector4f specularColor;

    public float shininess;
    public float reflectance;

    public Texture diffuseTexture;
    public Texture specularTexture;
    public Texture normalTexture;

    public Material() {
        this.ambientColor  = Constants.DEFAULT_COLOR;
        this.diffuseColor  = Constants.DEFAULT_COLOR;
        this.specularColor = Constants.DEFAULT_COLOR;
    }

    public Material(Vector4f ambient, Vector4f diffuse, Vector4f specular, float shininess) {
        this.ambientColor  = ambient;
        this.diffuseColor  = diffuse;
        this.specularColor = specular;
        this.shininess     = shininess;
    }

    public Material clone() throws CloneNotSupportedException {
        Material clone      = (Material) super.clone();
        clone.diffuseColor  = (Vector4f) diffuseColor.clone();
        clone.ambientColor  = (Vector4f) ambientColor.clone();
        clone.specularColor = (Vector4f) specularColor.clone();
        return clone;
    }
}
