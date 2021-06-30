package gfx;

import utils.Disposable;
import exceptions.OpenGLShaderCompilationException;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL20.*;
import static utils.Utils.readFromFile;

public class Shader implements Disposable {

    @Getter
    private int id = 0;

    public Shader(final String vertShaderCode, final String fragShaderCode) {
        try {
            int vert = createShader(GL_VERTEX_SHADER,   readFromFile(vertShaderCode));
            int frag = createShader(GL_FRAGMENT_SHADER, readFromFile(fragShaderCode));

            id = glCreateProgram();
            glAttachShader(id, vert);
            glAttachShader(id, frag);
            glLinkProgram(id);

            String err = glGetProgramInfoLog(id, glGetProgrami(id, GL_INFO_LOG_LENGTH));
            if (glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE) {
                throw new OpenGLShaderCompilationException(err);
            }

            glDeleteShader(vert);
            glDeleteShader(frag);
        } catch (OpenGLShaderCompilationException e) {
            Logger.getGlobal().log(Level.WARNING, e.getMessage());
        }
    }

    private int createShader(int type, String source) {
        int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);

        String err = glGetShaderInfoLog(shader, glGetShaderi(shader, GL_INFO_LOG_LENGTH));
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new OpenGLShaderCompilationException(err);
        }
        return shader;
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
