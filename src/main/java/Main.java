import core.*;
import gfx.*;
//import imgui.ImGui;
import imgui.internal.ImGui;

import lombok.SneakyThrows;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL32;

import static utils.Utils.ReadFromFile;

public class Main extends Application {

    public static void main(String[] args) {
        launch(new Main());
    }


    private Shader shader;

    DirectionalLight dl;
    @SneakyThrows
    @Override
    public void preProcess() {
        TextureManager.getInstance();
        Scene.init();
        GUI.init();
        GL32.glEnable(GL32.GL_DEPTH_TEST);
        dl = new DirectionalLight(new Vector3f(0.3f, -0.5f, 0.5f), new Vector3f(0.1f, 0.1f, 0.1f),
                new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(0.4f, 0.4f, 0.4f));
        shader = new Shader(Constants.SCENE_VERTEX_SHADER_PATH, Constants.SCENE_FRAGMENT_SHADER_PATH);

//        GL32.glEnable(GL32.GL_CULL_FACE);
//        GL32.glCullFace(GL32.GL_BACK);

    }


    @Override
    public void render(float delta) {
        if (ImGui.isMouseDown(1)) {
            Scene.FPSCamera.UpdateController(delta);
        }

        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, Scene.frameBuffer.getId());
//        GL32.glPolygonMode(GL32.GL_FRONT_AND_BACK, GL32.GL_LINE);
        GL32.glClearColor(0.1F, 0.1F, 0.1F, 1.0F);
        GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT | GL32.GL_STENCIL_BUFFER_BIT);

        if (Settings.EnableGrid) {
            SceneGrid.getInstance().render();
        }

        GL32.glUseProgram(shader.getId());
        shader.setUniformVec3("viewPos", Scene.FPSCamera.position);
        dl.apply(shader);
        shader.setUniformMat4("view", Scene.FPSCamera.getViewMatrix());
        shader.setUniformMat4("proj", Scene.FPSCamera.getProjectionMatrix());

        for (Model model : Scene.models) {
            model.draw(shader);
        }

        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, 0);
        GL32.glPolygonMode(GL32.GL_FRONT_AND_BACK, GL32.GL_FILL);

    }

    @Override
    public void process() {
        ImGui.showDemoWindow();
        GUI.renderMenuBar(Scene.frameBuffer);
        GUI.renderViewport(Scene.frameBuffer, Scene.FPSCamera);
        GUI.renderSceneItemsDock();
        GUI.renderProperties();
        GUI.renderLightProperties(dl, Scene.pointLights);

    }

    @Override
    public void postProcess() {
        SceneGrid.getInstance().dispose();
        TextureManager.getInstance().dispose();
    }
}
