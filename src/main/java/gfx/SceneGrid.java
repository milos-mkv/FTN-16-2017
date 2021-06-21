package gfx;

import core.Constants;
import core.Scene;
import lombok.Getter;
import org.lwjgl.opengl.GL32;

public class SceneGrid {

    private static SceneGrid sceneGrid;

    public static SceneGrid getInstance() {
        return (sceneGrid == null) ? sceneGrid = new SceneGrid() : sceneGrid;
    }

    @Getter
    private final int vao;

    @Getter
    private final int vbo;

    @Getter
    private final Shader shader;

    private SceneGrid() {
        shader = new Shader(Constants.SCENE_GRID_VERTEX_SHADER_PATH, Constants.SCENE_GRID_FRAGMENT_SHADER_PATH);

        vao = GL32.glGenVertexArrays();
        vbo = GL32.glGenBuffers();

        float[] vert = { 1, -1, 0, 1, 1, 0, -1, 1, 0, -1, -1, 0, 1, -1, 0, -1, 1, 0 };

        GL32.glBindVertexArray(vao);
        GL32.glBindBuffer(GL32.GL_ARRAY_BUFFER, vbo);
        GL32.glBufferData(GL32.GL_ARRAY_BUFFER, vert, GL32.GL_STATIC_DRAW);

        GL32.glEnableVertexAttribArray(0);
        GL32.glVertexAttribPointer(0, 3, GL32.GL_FLOAT, false, 0, 0);

        GL32.glBindVertexArray(0);
    }

    public void render() {
        GL32.glUseProgram(shader.getId());
        shader.setUniformMat4("view", Scene.FPSCamera.getViewMatrix());
        shader.setUniformMat4("proj", Scene.FPSCamera.getProjectionMatrix());

        GL32.glBindVertexArray(vao);
        GL32.glDrawArrays(GL32.GL_TRIANGLES, 0, 6);
        GL32.glBindVertexArray(0);

        GL32.glClear(GL32.GL_DEPTH_BUFFER_BIT);
    }

    public void dispose() {
        shader.dispose();
        GL32.glDeleteBuffers(vbo);
        GL32.glDeleteVertexArrays(vao);
    }
}
