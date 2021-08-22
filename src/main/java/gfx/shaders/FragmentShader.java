package gfx.shaders;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;

public class FragmentShader extends Shader {

    public FragmentShader(String code) {
        super(GL_FRAGMENT_SHADER, code);
    }
}
