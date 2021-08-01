package gfx.shaders;

import exceptions.OpenGLFailedToCompileShaderException;
import lombok.Data;
import utils.Disposable;

import static org.lwjgl.opengl.GL20.*;

@Data
public class Shader implements Disposable {

    protected final int id;
    protected String code;

    public Shader(int type, String code) {
        id = glCreateShader(type);
        glShaderSource(id, code);
        glCompileShader(id);

        String err = glGetShaderInfoLog(id, glGetShaderi(id, GL_INFO_LOG_LENGTH));
        if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new OpenGLFailedToCompileShaderException(err);
        }
        this.code = code;
    }

    @Override
    public void dispose() {
        glDeleteShader(id);
    }
}
