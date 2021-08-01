package gfx.shaders;

import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public class VertexShader extends Shader {

    public VertexShader(String code) {
        super(GL_VERTEX_SHADER, code);
    }

}
