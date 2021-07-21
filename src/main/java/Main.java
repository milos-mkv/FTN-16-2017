import core.Application;
import core.Constants;
import core.Scene;
import gfx.Grid;
import gfx.ShadowMap;
import gfx.SkyBox;
import gui.GUI;
import imgui.ImGui;
import managers.TextureManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30C.*;


public class Main extends Application {

    public static void main(String[] args) {
        launch(new Main());
    }

    @Override
    protected void onStart() {
        Scene.initialize();
        SkyBox.initialize();
        Grid.initialize();
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GUI.initialize();
        ShadowMap.initialize();
    }

    @Override
    protected void render(float delta) {
        if (ImGui.isMouseDown(1)) {
            Scene.getFPSCamera().updateController(delta);
        }

        glBindFramebuffer(GL_FRAMEBUFFER, ShadowMap.getDepthMapFBO());
        glViewport(0, 0, ShadowMap.SHADOW_WIDTH, ShadowMap.SHADOW_HEIGHT);

        glClear(GL_DEPTH_BUFFER_BIT);
        glUseProgram(ShadowMap.shader.getId());

        float lightAngleX = (float) Math.toDegrees(Math.acos(Scene.getDirectionalLight().getDirection().z));
        float lightAngleY = (float) Math.toDegrees(Math.asin(Scene.getDirectionalLight().getDirection().x));
        float lightAngleZ = 0; // new Vector3f(lightAngleX, lightAngleY, lightAngleZ)
        Matrix4f lightViewMatrix = new Matrix4f().lookAt(
            new Vector3f().set(Scene.getDirectionalLight().getDirection()).mul(-1), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0)
        );
        var projection = new Matrix4f().ortho(-20.0f, 20.f, -20.0f, 20.0f, -20.f, 20.f);

        var lightSpaceMatrix = new Matrix4f().set(projection).mul(lightViewMatrix);

        ShadowMap.shader.setUniformMat4("orthoProjectionMatrix", lightSpaceMatrix);

        Scene.getModels().forEach((key, value) -> value.draw(ShadowMap.shader));


        glViewport(0, 0, Constants.WINDOW_DEFAULT_WIDTH, Constants.WINDOW_DEFAULT_HEIGHT);

        glBindFramebuffer(GL_FRAMEBUFFER, Scene.getFrameBuffer().getId());
        glClearColor(Scene.ClearColor[0], Scene.ClearColor[1], Scene.ClearColor[2], Scene.ClearColor[3]);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

//        SkyBox.render();

        Grid.render();

        glUseProgram(Scene.getSceneShader().getId());
        Scene.getSceneShader().setUniformMat4("lightSpaceMatrix", lightSpaceMatrix);

        Scene.getSceneShader().setUniformVec3("lightPos", new Vector3f(-1.f, 4.f, -2.f));
        Scene.getSceneShader().setUniformVec3("viewPos", Scene.getFPSCamera().getPosition());
        Scene.getDirectionalLight().apply(Scene.getSceneShader());
        Scene.getSceneShader().setUniformMat4("view", Scene.getFPSCamera().getViewMatrix());
        Scene.getSceneShader().setUniformMat4("proj", Scene.getFPSCamera().getProjectionMatrix());
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, ShadowMap.getDepthMap());
        Scene.getModels().forEach((key, value) -> value.draw(Scene.getSceneShader()));

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    protected void renderImGui() {
        ImGui.showDemoWindow();
        GUI.render();
//        ImGui.begin("Depth");
//        ImGui.image(ShadowMapper.getDepthMap(), ImGui.getWindowSizeX(), ImGui.getWindowSizeY(), 0, 1, 1, 0);
//
//        ImGui.end();
    }

    @Override
    protected void onEnd() {
        Scene.dispose();
        TextureManager.dispose();
        SkyBox.dispose();
    }
}
