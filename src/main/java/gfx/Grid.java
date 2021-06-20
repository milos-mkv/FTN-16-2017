package gfx;

import org.lwjgl.opengl.GL32;

import static utils.Utils.ReadFromFile;

public abstract class Grid {

    private static int vao;
    private static int vbo;
    private static Shader shader;

    public static void init() {
        shader = new Shader(ReadFromFile("src/main/resources/shaders/grid.vert"), ReadFromFile("src/main/resources/shaders/grid.frag"));

        vao = GL32.glGenVertexArrays();
        vbo = GL32.glGenBuffers();

        float[] vert = {1, -1, 0, 1, 1, 0, -1, 1, 0, -1, -1, 0, 1, -1, 0, -1, 1, 0};

        GL32.glBindVertexArray(vao);
        GL32.glBindBuffer(GL32.GL_ARRAY_BUFFER, vbo);
        GL32.glBufferData(GL32.GL_ARRAY_BUFFER, vert, GL32.GL_STATIC_DRAW);

        GL32.glEnableVertexAttribArray(0);
        GL32.glVertexAttribPointer(0, 3, GL32.GL_FLOAT, false, 0, 0);

        GL32.glBindVertexArray(0);
    }

    public static void render(FirstPersonCameraController controller) {
        GL32.glUseProgram(shader.getId());
        shader.setUniformMat4("view", controller.getViewMatrix());
        shader.setUniformMat4("proj", controller.getProjectionMatrix());

        GL32.glBindVertexArray(vao);
        GL32.glDrawArrays(GL32.GL_TRIANGLES, 0, 6);
        GL32.glBindVertexArray(0);

        GL32.glClear(GL32.GL_DEPTH_BUFFER_BIT);
    }

    public static void dispose() {
        shader.dispose();
        GL32.glDeleteBuffers(vbo);
        GL32.glDeleteVertexArrays(vao);
    }
}
