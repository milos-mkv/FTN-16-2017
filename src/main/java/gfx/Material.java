package gfx;

import core.Constants;
import lombok.Data;
import org.joml.Vector3f;

@Data
public class Material {

    private Vector3f ambientColor;
    private Vector3f diffuseColor;
    private Vector3f specularColor;

    private float shininess;
    private float reflectance;

    private Texture diffuseTexture;
    private Texture specularTexture;
    private Texture normalTexture;

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

}
