package gfx;

import core.Scene;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public abstract class Grid {

    private Grid() { }

    private static Shader shader;
    private static int vao;
    private static int vbo;

    public static void initialize() {
        shader = new Shader("src/main/resources/shaders/newgrid.vert", "src/main/resources/shaders/newgrid.frag");

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

    public static void render() {

        glUseProgram(shader.getId());
        shader.setUniformMat4("view", Scene.getFPSCamera().getViewMatrix());
        shader.setUniformMat4("proj", Scene.getFPSCamera().getProjectionMatrix());

        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glBindVertexArray(0);

        glClear(GL_DEPTH_BUFFER_BIT);
    }

    public static void dispose() {
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
        shader.dispose();
    }

}
