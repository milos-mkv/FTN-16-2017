package gfx;

import core.Scene;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL32;

public abstract class SkyBox {

    @Getter
    private static int vao;

    private static int vbo;

    @Getter
    @Setter
    private static CubeMap cubeMap;

    public static void initialize() {
        float[] skyboxVertices = {
                -1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f,  1.0f, -1.0f, -1.0f,  1.0f, -1.0f,
                -1.0f, -1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f,  1.0f, -1.0f, -1.0f,  1.0f,
                 1.0f, -1.0f, -1.0f,  1.0f, -1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f, -1.0f,  1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f,  1.0f, -1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f,
                -1.0f,  1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f,  1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f
        };
        vao = GL32.glGenVertexArrays();
        vbo = GL32.glGenBuffers();
        GL32.glBindVertexArray(vao);
        GL32.glBindBuffer(GL32.GL_ARRAY_BUFFER, vbo);
        GL32.glBufferData(GL32.GL_ARRAY_BUFFER, skyboxVertices, GL32.GL_STATIC_DRAW);
        GL32.glVertexAttribPointer(0, 3, GL32.GL_FLOAT, false, 0, 0);
        GL32.glEnableVertexAttribArray(0);
        GL32.glBindVertexArray(0);
    }

    public static void render() {
        GL32.glDisable(GL32.GL_STENCIL_TEST);
        GL32.glDepthFunc(GL32.GL_LEQUAL);
        GL32.glUseProgram(Scene.getSkyboxShader().getId());

        Scene.getSkyboxShader().setUniformMat4("proj", Scene.getFPSCamera().getProjectionMatrix());
        Scene.getSkyboxShader().setUniformMat4("view",
                new Matrix4f().set(
                new Matrix3f().set(Scene.getFPSCamera().getViewMatrix())));

        GL32.glBindVertexArray(vao);
        GL32.glActiveTexture(GL32.GL_TEXTURE0);
        GL32.glUniform1i(GL32.glGetUniformLocation(Scene.getSkyboxShader().getId(), "skybox"), 0);

        GL32.glBindTexture(GL32.GL_TEXTURE_CUBE_MAP, cubeMap.getId());
        GL32.glDrawArrays(GL32.GL_TRIANGLES, 0, 36);
        GL32.glBindVertexArray(0);

        GL32.glDepthFunc(GL32.GL_LESS);
        GL32.glEnable(GL32.GL_STENCIL_TEST);
    }

    public static void dispose() {
        GL32.glDeleteBuffers(vbo);
        GL32.glDeleteVertexArrays(vao);
    }

}
