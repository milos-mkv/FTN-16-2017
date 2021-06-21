package gfx;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL32;

import static utils.Utils.Assert;
import static utils.Utils.ReadFromFile;

public class Shader {
    @Getter
    private int id = 0;
    @Getter
    private String message;

    public Shader(final String vertShaderCode, final String fragShaderCode) {
        try {
            int vert = createShader(GL32.GL_VERTEX_SHADER, ReadFromFile(vertShaderCode));
            int frag = createShader(GL32.GL_FRAGMENT_SHADER, ReadFromFile(fragShaderCode));

            id = GL32.glCreateProgram();
            GL32.glAttachShader(id, vert);
            GL32.glAttachShader(id, frag);
            GL32.glLinkProgram(id);

            String err = GL32.glGetProgramInfoLog(id, GL32.glGetProgrami(id, GL32.GL_INFO_LOG_LENGTH));
            Assert(GL32.glGetProgrami(id, GL32.GL_LINK_STATUS) != GL32.GL_FALSE, err);

            GL32.glDeleteShader(vert);
            GL32.glDeleteShader(frag);
            message = "Shader program compiled successfully!";
        } catch (Exception e) {
            System.out.println(e);
            message = e.getMessage();
        }
    }

    private int createShader(int type, String source) {
        int shader = GL32.glCreateShader(type);
        GL32.glShaderSource(shader, source);
        GL32.glCompileShader(shader);

        String err = GL32.glGetShaderInfoLog(shader, GL32.glGetShaderi(shader, GL32.GL_INFO_LOG_LENGTH));
        Assert(GL32.glGetShaderi(shader, GL32.GL_COMPILE_STATUS) != GL32.GL_FALSE, err);

        return shader;
    }

    public void setUniformBoolean(final String name, int value) {
        GL32.glUniform1i(GL32.glGetUniformLocation(id, name), value);
    }

    public void setUniformInt(final String name, int value) {
        GL32.glUniform1i(GL32.glGetUniformLocation(id, name), value);
    }

    public void setUniformFloat(final String name, float value) {
        GL32.glUniform1f(GL32.glGetUniformLocation(id, name), value);
    }

    public void setUniformVec2(final String name, Vector2f value) {
        GL32.glUniform2f(GL32.glGetUniformLocation(id, name), value.x, value.y);
    }

    public void setUniformVec3(final String name, Vector3f value) {
        GL32.glUniform3f(GL32.glGetUniformLocation(id, name), value.x, value.y, value.z);
    }

    public void setUniformVec4(final String name, Vector4f value) {
        GL32.glUniform4f(GL32.glGetUniformLocation(id, name), value.x, value.y, value.z, value.w);
    }

    public void setUniformMat4(final String name, Matrix4f value) {
        float[] buffer = new float[16];
        value.get(buffer);
        GL32.glUniformMatrix4fv(GL32.glGetUniformLocation(id, name), false, buffer);
    }

    public void dispose() {
        GL32.glDeleteProgram(id);
    }
}
