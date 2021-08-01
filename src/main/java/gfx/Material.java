package gfx;

import core.Constants;
import lombok.Data;
import org.joml.Vector3f;

@Data
public class Material {

    private String name;

    private Vector3f ambientColor;
    private Vector3f diffuseColor;
    private Vector3f specularColor;

    private float shininess;
    private float reflectance;

    private Texture diffuseTexture;
    private Texture specularTexture;
    private Texture normalTexture;

    public Material() {
        this.ambientColor  = new Vector3f(1, 1, 1);
        this.diffuseColor  = new Vector3f(1, 1, 1);
        this.specularColor = new Vector3f(1, 1, 1);
    }

    public Material(Vector3f ambient, Vector3f diffuse, Vector3f specular, float shininess) {
        this.ambientColor  = ambient;
        this.diffuseColor  = diffuse;
        this.specularColor = specular;
        this.shininess     = shininess;
    }

}
