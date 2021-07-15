import core.Application;
import core.Constants;
import core.Scene;
import gfx.Grid;
import gfx.ShadowMapper;
import gfx.SkyBox;
import gui.GUI;
import imgui.ImGui;
import managers.TextureManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
//        InputStream stream = Main.class.getClassLoader().getResourceAsStream("logging.properties");
//        try {
//            LogManager.getLogManager().readConfiguration(stream);
//            var handler = new FileHandler("tmp.log");
//            Logger.getGlobal().addHandler(handler);
////            handler.setFormatter(new SimpleFormatter());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Scene.initialize();
        SkyBox.initialize();
        Grid.initialize();
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GUI.initialize();

        ShadowMapper.initialize();

    }

    @Override
    protected void render(float delta) {
        if (ImGui.isMouseDown(1)) {
            Scene.getFPSCamera().updateController(delta);
        }
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        float near_plane = 1.0f, far_plane = 7.5f;
        var lightProjection = new Matrix4f().ortho(-20.0f, 20.f, -20.0f, 20.0f, near_plane, far_plane);
        var lightView = new Matrix4f().lookAt(
                new Vector3f(-1.f, 4.f, -2.f),
                new Vector3f(0.0f, 0.0f, 0.0f),
                new Vector3f(0.0f, 1.0f, 0.0f));

        Matrix4f lightSpaceMatrix = new Matrix4f();
        lightSpaceMatrix.set(lightProjection).mul(lightView);


        glUseProgram(ShadowMapper.shader.getId());
        ShadowMapper.shader.setUniformMat4("lightSpaceMatrix", lightSpaceMatrix);


        glViewport(0, 0, ShadowMapper.SHADOW_WIDTH, ShadowMapper.SHADOW_HEIGHT);
        glBindFramebuffer(GL_FRAMEBUFFER, ShadowMapper.getDepthMapFBO());
        glClear(GL_DEPTH_BUFFER_BIT);
        glCullFace(GL_FRONT);
        Scene.getModels().forEach((key, value) -> value.draw(ShadowMapper.shader));
        glCullFace(GL_BACK);

        glViewport(0, 0, Constants.WINDOW_DEFAULT_WIDTH, Constants.WINDOW_DEFAULT_HEIGHT);

        glBindFramebuffer(GL_FRAMEBUFFER, Scene.getFrameBuffer().getId());
        glClearColor(Scene.ClearColor[0], Scene.ClearColor[1], Scene.ClearColor[2], Scene.ClearColor[3]);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        SkyBox.render();

        Grid.render();

        glUseProgram(Scene.getSceneShader().getId());
        Scene.getSceneShader().setUniformMat4("lightSpaceMatrix", lightSpaceMatrix);
        Scene.getSceneShader().setUniformVec3("lightPos", new Vector3f(-1.f, 4.f, -2.f));
        Scene.getSceneShader().setUniformVec3("viewPos", Scene.getFPSCamera().getPosition());
        Scene.getDirectionalLight().apply(Scene.getSceneShader());
        Scene.getSceneShader().setUniformMat4("view", Scene.getFPSCamera().getViewMatrix());
        Scene.getSceneShader().setUniformMat4("proj", Scene.getFPSCamera().getProjectionMatrix());
//        glActiveTexture(GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_2D, ShadowMapper.getDepthMap());
        Scene.getModels().forEach((key, value) -> value.draw(Scene.getSceneShader()));
//
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
