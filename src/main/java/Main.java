import core.Application;
import core.Scene;
import gfx.Grid;
import gfx.Shader;
import gfx.SkyBox;
import gui.GUI;
import imgui.ImGui;
import managers.TextureManager;
import org.lwjgl.opengl.GL32;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30C.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30C.glBindFramebuffer;


public class Main extends Application {

    public static void main(String[] args) {
        launch(new Main());
    }



    @Override
    protected void onStart() {


        InputStream stream = Main.class.getClassLoader().getResourceAsStream("logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(stream);
            var handler = new FileHandler("tmp.log");
            Logger.getGlobal().addHandler(handler);
            handler.setFormatter(new SimpleFormatter());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene.initialize();
        SkyBox.initialize();
        Grid.initialize();
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(2);
        GUI.initialize();
    }

    @Override
    protected void render(float delta) {
        if (ImGui.isMouseDown(1)) {
            Scene.getFPSCamera().updateController(delta);
        }
        
        glBindFramebuffer(GL_FRAMEBUFFER, Scene.getFrameBuffer().getId());
        glClearColor(Scene.ClearColor[0], Scene.ClearColor[1], Scene.ClearColor[2], Scene.ClearColor[3]);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

//        SkyBox.render();

        Grid.render();


        glUseProgram(Scene.getSceneShader().getId());
        Scene.getSceneShader().setUniformVec3("viewPos", Scene.getFPSCamera().getPosition());
        Scene.getDirectionalLight().apply(Scene.getSceneShader());
        Scene.getSceneShader().setUniformMat4("view", Scene.getFPSCamera().getViewMatrix());
        Scene.getSceneShader().setUniformMat4("proj", Scene.getFPSCamera().getProjectionMatrix());


        Scene.getModels().forEach((key, value) -> value.draw(Scene.getSceneShader()));


        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    protected void renderImGui() {
        ImGui.showDemoWindow();
        GUI.render();
    }

    @Override
    protected void onEnd() {
        Scene.dispose();
        TextureManager.dispose();
        SkyBox.dispose();
    }
}
