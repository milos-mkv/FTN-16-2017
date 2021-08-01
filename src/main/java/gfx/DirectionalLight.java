package gfx;

import lombok.Data;

import lombok.EqualsAndHashCode;
import org.joml.Vector3f;

@EqualsAndHashCode(callSuper = true)
@Data
public class DirectionalLight extends Light {

    private Vector3f direction;

    public DirectionalLight(Vector3f direction, Vector3f ambient, Vector3f diffuse, Vector3f specular) {
        super(ambient, diffuse, specular);
        this.direction = direction;
    }

    public void apply(ShaderProgram shaderProgram) {
        shaderProgram.setUniformVec3("dirLight.direction", direction);
        shaderProgram.setUniformVec3("dirLight.ambient",   ambient);
        shaderProgram.setUniformVec3("dirLight.diffuse",   diffuse);
        shaderProgram.setUniformVec3("dirLight.specular",  specular);
    }
}
