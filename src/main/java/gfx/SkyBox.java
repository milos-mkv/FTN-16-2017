package gfx;

import core.Constants;
import core.Scene;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL32;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;


public abstract class SkyBox {

    private static int vao;
    private static int vbo;
    private static Shader shader;

    @Getter
    @Setter
    private static CubeMap cubemap;

    private SkyBox() { }

    public static void initialize() {
        shader = new Shader(Constants.DEFAULT_SKYBOX_VERTEX_SHADER_PATH, Constants.DEFAULT_SKYBOX_FRAGMENT_SHADER_PATH);
        cubemap = new CubeMap(Constants.DEFAULT_SKYBOX_FACES);

        var skyboxVertices = new float[] {
                -1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f,  1.0f, -1.0f, -1.0f,  1.0f, -1.0f,
                -1.0f, -1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f,  1.0f, -1.0f, -1.0f,  1.0f,
                 1.0f, -1.0f, -1.0f,  1.0f, -1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f, -1.0f,  1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f,  1.0f, -1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f,
                -1.0f,  1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f,  1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f
        };
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, skyboxVertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);
        glBindVertexArray(0);
    }

    public static void render() {
        glDisable(GL_STENCIL_TEST);
        glDepthFunc(GL_LEQUAL);
        glUseProgram(shader.getId());

        shader.setUniformMat4("proj", Scene.getFPSCamera().getProjectionMatrix());
        shader.setUniformMat4("view",
                new Matrix4f().set(
                new Matrix3f().set(Scene.getFPSCamera().getViewMatrix())));

        glBindVertexArray(vao);
        glActiveTexture(GL_TEXTURE0);
        glUniform1i(glGetUniformLocation(shader.getId(), "skybox"), 0);

        glBindTexture(GL_TEXTURE_CUBE_MAP, cubemap.getId());
        glDrawArrays(GL_TRIANGLES, 0, 36);
        glBindVertexArray(0);

        glDepthFunc(GL_LESS);
        glEnable(GL_STENCIL_TEST);
    }

    public static void dispose() {
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
        shader.dispose();
        cubemap.dispose();
    }

}
