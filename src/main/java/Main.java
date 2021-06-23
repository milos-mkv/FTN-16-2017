import core.Application;
import core.Scene;
import gfx.CubeMap;
import gfx.Model;
import gfx.SkyBox;
import gui.GUI;
import imgui.ImGui;
import managers.TextureManager;
import org.lwjgl.opengl.GL32;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    public static void main(String[] args) {
        launch(new Main());
    }

    @Override
    protected void onStart() {
        GUI.initialize();
        Scene.initialize();
        TextureManager.initialize();
        SkyBox.initialize();
        GL32.glEnable(GL32.GL_MULTISAMPLE);
        GL32.glEnable(GL32.GL_DEPTH_TEST);
    }

    @Override
    protected void render(float delta) {
        if (ImGui.isMouseDown(1)) {
            Scene.getFPSCamera().UpdateController(delta);
        }

        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, Scene.getFrameBuffer().getId());
        GL32.glClearColor(0.1F, 0.1F, 0.1F, 1.0F);
        GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT | GL32.GL_STENCIL_BUFFER_BIT);

//        SkyBox.render();

        GL32.glUseProgram(Scene.getSceneShader().getId());
        Scene.getSceneShader().setUniformVec3("viewPos", Scene.getFPSCamera().position);
        Scene.getDirectionalLight().apply(Scene.getSceneShader());
        Scene.getSceneShader().setUniformMat4("view", Scene.getFPSCamera().getViewMatrix());
        Scene.getSceneShader().setUniformMat4("proj", Scene.getFPSCamera().getProjectionMatrix());


        Scene.getModels().forEach((key, value) -> value.draw(Scene.getSceneShader()));

        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, 0);
    }

    @Override
    protected void renderImGui() {
        ImGui.showDemoWindow();
        GUI.renderMenuBar();
        GUI.renderViewport();
        GUI.renderLightProperties();
        GUI.renderSceneItemsDock();
    }

    @Override
    protected void onEnd() {
        Scene.dispose();
        TextureManager.dispose();
        SkyBox.dispose();
    }
}
