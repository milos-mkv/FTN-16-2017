import core.Application;
import core.GUI;
import core.Scene;
import gfx.*;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import lombok.SneakyThrows;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL32;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

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
        texture =  new Texture("src/main/resources/models/diffuse.jpg");
        texture1 =  new Texture("src/main/resources/models/specular.jpg");

        gridShader = new Shader(ReadFromFile("src/main/resources/shaders/grid.vert"), ReadFromFile("src/main/resources/shaders/grid.frag"));
        shader = new Shader(ReadFromFile("src/main/resources/shaders/shader.vert"), ReadFromFile("src/main/resources/shaders/shader.frag"));
        fb = new FrameBuffer(1280, 769);
        controller = new FirstPersonCameraController(45, 1280.F / 769.F, 0.1F, 100.0F);
        controller.position.set(0, 0, 0);
        controller.UpdateVectors();

//        GL32.glEnable(GL32.GL_CULL_FACE);
//        GL32.glCullFace(GL32.GL_BACK);


    }

    @Override
    public void render(float delta) {
        if (ImGui.isMouseDown(1))
            controller.UpdateController(delta);
        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, fb.getId());

        GL32.glClearColor(0.1F, 0.1F, 0.1F, 1.0F);
        GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT | GL32.GL_STENCIL_BUFFER_BIT);

        GL32.glUseProgram(shader.getId());
        shader.setUniformMat4("view", controller.getViewMatrix());
        shader.setUniformMat4("proj", controller.getProjectionMatrix());

        GL32.glActiveTexture(GL32.GL_TEXTURE0);
        GL32.glBindTexture(GL32.GL_TEXTURE_2D, texture.getId());
        GL32.glActiveTexture(GL32.GL_TEXTURE1);
        GL32.glBindTexture(GL32.GL_TEXTURE_2D, texture1.getId());


        for(Model model : Scene.models) {
            model.draw(shader);
        }

        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, 0);


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
