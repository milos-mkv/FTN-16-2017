package gfx;

import core.Constants;
import org.joml.Vector3f;

public class Material implements Cloneable {

    public Vector3f ambientColor;
    public Vector3f diffuseColor;
    public Vector3f specularColor;

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

    public Material(Vector3f ambient, Vector3f diffuse, Vector3f specular, float shininess) {
        this.ambientColor  = ambient;
        this.diffuseColor  = diffuse;
        this.specularColor = specular;
        this.shininess     = shininess;
    }

    public Material clone() throws CloneNotSupportedException {
        Material clone      = (Material) super.clone();
        clone.diffuseColor  = (Vector3f) diffuseColor.clone();
        clone.ambientColor  = (Vector3f) ambientColor.clone();
        clone.specularColor = (Vector3f) specularColor.clone();
        return clone;
    }
}
