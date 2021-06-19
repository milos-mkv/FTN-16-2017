import core.Application;
import core.GUI;
import core.Scene;
import core.Settings;
import gfx.*;
import imgui.ImGui;

import lombok.SneakyThrows;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL32;

import static utils.Utils.ReadFromFile;

public class Main extends Application {

    public static void main(String[] args) {
        launch(new Main());
    }


    private Shader shader, gridShader;
    private FrameBuffer fb;
    private FirstPersonCameraController controller;

    int vao, vbo;

    Texture texture, texture1;


    @SneakyThrows
    @Override
    public void preProcess() {


        GUI.init();

        GL32.glEnable(GL32.GL_DEPTH_TEST);
        Vector3f a = new Vector3f(1, 1, 1);
        Vector3f b = (Vector3f) a.clone();
        b.x = 2;
        System.out.println(a.x);
        texture = new Texture("src/main/resources/models/diffuse.jpg");
        texture1 = new Texture("src/main/resources/models/specular.jpg");

        gridShader = new Shader(ReadFromFile("src/main/resources/shaders/grid.vert"), ReadFromFile("src/main/resources/shaders/grid.frag"));
        shader = new Shader(ReadFromFile("src/main/resources/shaders/shader.vert"), ReadFromFile("src/main/resources/shaders/shader.frag"));
        fb = new FrameBuffer(1280, 769);
        controller = new FirstPersonCameraController(45, 1280.F / 769.F, 0.1F, 100.0F);
        controller.position.set(0, 0, 0);
        controller.UpdateVectors();

        GL32.glEnable(GL32.GL_CULL_FACE);
        GL32.glCullFace(GL32.GL_BACK);
        vao = GL32.glGenVertexArrays();
        vbo = GL32.glGenBuffers();

        GL32.glBindVertexArray(vao);
        float[] vert = {
                1, -1, 0,
                1, 1, 0,
                -1, 1, 0,
                -1, -1, 0,
                1, -1, 0,
                -1, 1, 0
        };
        GL32.glBindBuffer(GL32.GL_ARRAY_BUFFER, vbo);
        GL32.glBufferData(GL32.GL_ARRAY_BUFFER, vert, GL32.GL_STATIC_DRAW);

        // vertex positions
        GL32.glEnableVertexAttribArray(0);
        GL32.glVertexAttribPointer(0, 3, GL32.GL_FLOAT, false, 0, 0);

        GL32.glBindVertexArray(0);
        controller.position.y = 1;
        controller.UpdateVectors();
    }

    @Override
    public void render(float delta) {

        if (ImGui.isMouseDown(1))
            controller.UpdateController(delta);
        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, fb.getId());
//        GL32.glPolygonMode(GL32.GL_FRONT_AND_BACK, GL32.GL_LINE);
        GL32.glClearColor(0.1F, 0.1F, 0.1F, 1.0F);
        GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT | GL32.GL_STENCIL_BUFFER_BIT);

        if(Settings.EnableGrid) {
            GL32.glUseProgram(gridShader.getId());
            gridShader.setUniformMat4("view", controller.getViewMatrix());
            gridShader.setUniformMat4("proj", controller.getProjectionMatrix());

            GL32.glBindVertexArray(vao);
            GL32.glDrawArrays(GL32.GL_TRIANGLES, 0, 6);
            GL32.glBindVertexArray(0);

            GL32.glClear(GL32.GL_DEPTH_BUFFER_BIT);
        }
        GL32.glUseProgram(shader.getId());
        shader.setUniformMat4("view", controller.getViewMatrix());
        shader.setUniformMat4("proj", controller.getProjectionMatrix());

        GL32.glActiveTexture(GL32.GL_TEXTURE0);
        GL32.glBindTexture(GL32.GL_TEXTURE_2D, texture.getId());
        GL32.glActiveTexture(GL32.GL_TEXTURE1);
        GL32.glBindTexture(GL32.GL_TEXTURE_2D, texture1.getId());


        for (Model model : Scene.models) {
            model.draw(shader);
        }

        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, 0);
        GL32.glPolygonMode(GL32.GL_FRONT_AND_BACK, GL32.GL_FILL);

    }

    @Override
    public void process() {
        ImGui.showDemoWindow();
        GUI.renderMenuBar(fb);
        GUI.renderViewport(fb, controller);
        GUI.renderSceneItemsDock();
        GUI.renderProperties();
    }

    @Override
    public void postProcess() {

    }
}
