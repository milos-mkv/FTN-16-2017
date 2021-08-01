package gfx;

import core.Scene;
import managers.ShaderProgramManager;
import utils.Disposable;
import utils.Renderable;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Grid implements Disposable, Renderable {

    private static Grid grid;

    public static Grid getInstance() {
         return grid == null ? grid = new Grid() : grid;
    }

    private final int vao;
    private final int vbo;

    private Grid() {
        vao = glGenVertexArrays();
        vbo = glGenBuffers();

        var vert = new float[] { 1, -1, 0, 1, 1, 0, -1, 1, 0, -1, -1, 0, 1, -1, 0, -1, 1, 0 };

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vert, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        glBindVertexArray(0);
    }

    @Override
    public void render() {
        ShaderProgram program = ShaderProgramManager.getInstance().get("GRID SHADER");
        glUseProgram(program.getId());
        program.setUniformMat4("view", Scene.getInstance().getCamera().getViewMatrix());
        program.setUniformMat4("proj", Scene.getInstance().getCamera().getProjectionMatrix());

        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glBindVertexArray(0);

        glClear(GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void dispose() {
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
    }

}
