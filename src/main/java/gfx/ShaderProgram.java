package gfx;

import exceptions.OpenGLFailedToCompileShaderException;
import exceptions.OpenGLFailedToLinkShaderProgramException;
import gfx.shaders.FragmentShader;
import gfx.shaders.Shader;
import gfx.shaders.VertexShader;
import lombok.Data;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import utils.Disposable;

import static org.lwjgl.opengl.GL20.*;

@Data
public class ShaderProgram implements Disposable {

    private int id;
    private Shader vert;
    private Shader frag;

    public ShaderProgram(final String vertShaderCode, final String fragShaderCode)
            throws OpenGLFailedToLinkShaderProgramException, OpenGLFailedToCompileShaderException {

        vert = new VertexShader(vertShaderCode);
        frag = new FragmentShader(fragShaderCode);

        id = glCreateProgram();
        glAttachShader(id, vert.getId());
        glAttachShader(id, frag.getId());
        glLinkProgram(id);

        String err = glGetProgramInfoLog(id, glGetProgrami(id, GL_INFO_LOG_LENGTH));
        if (glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE) {
            throw new OpenGLFailedToLinkShaderProgramException(err);
        }

        vert.dispose();
        frag.dispose();

    }

    public void setUniformBoolean(String name, int value) {
        glUniform1i(glGetUniformLocation(id, name), value);
    }

    public void setUniformInt(String name, int value) {
        glUniform1i(glGetUniformLocation(id, name), value);
    }

    public void setUniformFloat(String name, float value) {
        glUniform1f(glGetUniformLocation(id, name), value);
    }

    public void setUniformVec2(String name, Vector2f value) {
        glUniform2f(glGetUniformLocation(id, name), value.x, value.y);
    }

    public void setUniformVec3(String name, Vector3f value) {
        glUniform3f(glGetUniformLocation(id, name), value.x, value.y, value.z);
    }

    public void setUniformVec4(String name, Vector4f value) {
        glUniform4f(glGetUniformLocation(id, name), value.x, value.y, value.z, value.w);
    }

    public void setUniformMat4(String name, Matrix4f value) {
        var buffer = new float[16];
        value.get(buffer);
        glUniformMatrix4fv(glGetUniformLocation(id, name), false, buffer);
    }

    @Override
    public void dispose() {
        glDeleteProgram(id);
    }
}
