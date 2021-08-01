package gfx;

import core.Constants;
import core.Scene;
import lombok.Getter;
import lombok.Setter;
import managers.ShaderProgramManager;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import utils.Disposable;
import utils.Renderable;

import static org.lwjgl.opengl.GL30.*;


public class SkyBox implements Disposable, Renderable {

    private static SkyBox skyBox;

    public static SkyBox getInstance() {
        return skyBox == null ? skyBox = new SkyBox() : skyBox;
    }

    private final int vao;
    private final int vbo;

    @Getter
    @Setter
    private CubeMap cubemap;

    private SkyBox() {
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

    @Override
    public void render() {
        ShaderProgram program = ShaderProgramManager.getInstance().get("SKYBOX SHADER");

        glDisable(GL_STENCIL_TEST);
        glDepthFunc(GL_LEQUAL);
        glUseProgram(program.getId());

        program.setUniformMat4("proj", Scene.getInstance().getCamera().getProjectionMatrix());
        program.setUniformMat4("view",
                new Matrix4f().set(
                new Matrix3f().set(Scene.getInstance().getCamera().getViewMatrix())));

        glBindVertexArray(vao);
        glActiveTexture(GL_TEXTURE0);
        glUniform1i(glGetUniformLocation(program.getId(), "skybox"), 0);

        glBindTexture(GL_TEXTURE_CUBE_MAP, cubemap.getId());
        glDrawArrays(GL_TRIANGLES, 0, 36);
        glBindVertexArray(0);

        glDepthFunc(GL_LESS);
        glEnable(GL_STENCIL_TEST);
    }

    @Override
    public void dispose() {
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
        cubemap.dispose();
    }

}
