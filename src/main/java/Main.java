import core.Application;
import core.Constants;
import core.Scene;
import core.Settings;
import gfx.Grid;
import gfx.ShaderProgram;
import gfx.ShadowMap;
import gfx.SkyBox;
import gui.GUI;
import imgui.ImGui;
import managers.ModelManager;
import managers.ShaderProgramManager;
import managers.TextureManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30C.*;

public class Main extends Application {

    public static void main(String[] args) {
        launch(new Main());
    }

    @Override
    protected void onStart() {
        ShaderProgramManager.getInstance();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GUI.initialize();
        ShadowMap.initialize();
        ModelManager.getInstance();
    }

    @Override
    protected void render(float delta) {
        Scene scene = Scene.getInstance();

        if (ImGui.isMouseDown(1)) {
            scene.getCamera().updateController(delta);
        }

        glBindFramebuffer(GL_FRAMEBUFFER, ShadowMap.getDepthMapFBO());
        glViewport(0, 0, ShadowMap.SHADOW_WIDTH, ShadowMap.SHADOW_HEIGHT);

        glClear(GL_DEPTH_BUFFER_BIT);
        glUseProgram(ShadowMap.shaderProgram.getId());

        Matrix4f lightViewMatrix = new Matrix4f().lookAt(
            new Vector3f().set(scene.getDirectionalLight().getDirection()).mul(-1), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0)
        );
        var projection = new Matrix4f().ortho(-20.0f, 20.f, -20.0f, 20.0f, -20.f, 20.f);

        var lightSpaceMatrix = new Matrix4f().set(projection).mul(lightViewMatrix);

        ShadowMap.shaderProgram.setUniformMat4("orthoProjectionMatrix", lightSpaceMatrix);

        scene.getModels().forEach((key, value) -> value.draw(ShadowMap.shaderProgram));


        glViewport(0, 0, Constants.WINDOW_DEFAULT_WIDTH, Constants.WINDOW_DEFAULT_HEIGHT);
        glBindFramebuffer(GL_FRAMEBUFFER, scene.getFrameBuffer().getId());
        glClearColor(Scene.ClearColor[0], Scene.ClearColor[1], Scene.ClearColor[2], Scene.ClearColor[3]);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        if(Settings.ToggleSkyBox) {
            SkyBox.getInstance().render();
        }
        if(Settings.ToggleGrid) {
            Grid.getInstance().render();
        }

        ShaderProgram program = ShaderProgramManager.getInstance().get("SCENE SHADER");

        glUseProgram(program.getId());
        program.setUniformMat4("lightSpaceMatrix", lightSpaceMatrix);

        program.setUniformVec3("lightPos", new Vector3f().set(scene.getDirectionalLight().getDirection()).mul(-1));
        program.setUniformVec3("viewPos", scene.getCamera().getPosition());
        scene.getDirectionalLight().apply(program);

        program.setUniformMat4("view", scene.getCamera().getViewMatrix());
        program.setUniformMat4("proj", scene.getCamera().getProjectionMatrix());

        program.setUniformInt("shadowMap", 0);
        program.setUniformInt("diffuseTexture", 1);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, ShadowMap.getDepthMap());
        scene.getModels().forEach((key, value) -> value.draw(program));

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    protected void renderImGui() {
        ImGui.showDemoWindow();
        GUI.render();
    }

    @Override
    protected void onEnd() {
        ShaderProgramManager.getInstance().dispose();
        TextureManager.getInstance().dispose();
        ModelManager.getInstance().dispose();
        Scene.getInstance().dispose();
        SkyBox.getInstance().dispose();
        Grid.getInstance().dispose();
    }
}
