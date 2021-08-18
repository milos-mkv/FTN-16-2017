import core.*;
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
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30C.*;

public class Main extends Application {

    public static void main(String[] args) {
        launch(new Main());
    }

    private GUI gui;

    @Override
    protected void onStart() {
        ShaderProgramManager.getInstance();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glHint (GL_LINE_SMOOTH_HINT, GL_NICEST);
        gui = new GUI();

        ModelManager.getInstance();
        glEnable(GL_STENCIL_TEST);
    }

    @Override
    protected void render(float delta) {
        Scene scene = Scene.getInstance();

        if (ImGui.isMouseDown(1)) {
            scene.getCamera().updateController(delta);
        }

        glBindFramebuffer(GL_FRAMEBUFFER, ShadowMap.getInstance().getDepthMapFBO());
        glViewport(0, 0, ShadowMap.SHADOW_WIDTH, ShadowMap.SHADOW_HEIGHT);

        glClear(GL_DEPTH_BUFFER_BIT);

        ShaderProgram shadowShader = ShaderProgramManager.getInstance().get("SHADOW SHADER");

        glUseProgram(shadowShader.getId());

        Matrix4f lightViewMatrix = new Matrix4f().lookAt(
                new Vector3f()
                        .set(scene.getDirectionalLight().getDirection())
                        .mul(-1),
                new Vector3f(0, 0, 0),
                new Vector3f(0, 1, 0)
        );
        var projection = new Matrix4f().ortho(-20.0f, 20.f, -20.0f, 20.0f, -20.f, 20.f);

        var lightSpaceMatrix = new Matrix4f().set(projection).mul(lightViewMatrix);

        shadowShader.setUniformMat4("orthoProjectionMatrix", lightSpaceMatrix);

        scene.getModels().forEach((key, value) -> value.draw(shadowShader));


        glViewport(0, 0, Constants.FRAMEBUFFER_WIDTH, Constants.FRAMEBUFFER_HEIGHT);
        if(Settings.EnableMSAA.get()) {
            glBindFramebuffer(GL_FRAMEBUFFER, scene.msFrameBuffer.getId());

        } else {
            glBindFramebuffer(GL_FRAMEBUFFER, scene.getFrameBuffer().getId());

        }
        glClearColor(Scene.ClearColor[0], Scene.ClearColor[1], Scene.ClearColor[2], Scene.ClearColor[3]);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        if (Settings.ToggleSkyBox.get()) {
            SkyBox.getInstance().render();
        }
        if (Settings.ToggleGrid.get()) {
            Grid.getInstance().render();
        }

        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        glStencilFunc(GL_ALWAYS, 1, 0xFF);
        glStencilMask(0xFF);


        ShaderProgram program = ShaderProgramManager.getInstance().get("SCENE SHADER");
        if(Settings.EnableLinePolygonMode.get()) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }
        if(Settings.EnableFaceCulling.get()) {
            GL11.glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
        }
        glUseProgram(program.getId());
        program.setUniformMat4("lightSpaceMatrix", lightSpaceMatrix);
        program.setUniformBoolean("isUsingDirectionalLight", Settings.EnableDirectionalLight.get() ? 1 : 0);
        program.setUniformVec3("lightPos", new Vector3f().set(scene.getDirectionalLight().getDirection()).mul(-1));
        program.setUniformVec3("viewPos", scene.getCamera().getPosition());
        scene.getDirectionalLight().apply(program);

        program.setUniformMat4("view", scene.getCamera().getViewMatrix());
        program.setUniformMat4("proj", scene.getCamera().getProjectionMatrix());

        program.setUniformInt("shadowMap", 0);
        program.setUniformInt("diffuseTexture", 1);
        program.setUniformInt("specularTexture", 2);
        program.setUniformInt("normalTexture", 3);

        program.setUniformBoolean("isUsingShadows", Settings.EnableShadows.get() ? 1 : 0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, ShadowMap.getInstance().getDepthMap());

        scene.getModels().forEach((key, value) -> value.draw(program));

        if (scene.getModels().size() > 0 && scene.getSelectedModel() != null) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            glLineWidth(10);
            glStencilFunc(GL_NOTEQUAL, 1, 0xFF);
            glStencilMask(0x00);
            glDisable(GL_DEPTH_TEST);

            ShaderProgram p = ShaderProgramManager.getInstance().get("BORDER SHADER");

            glUseProgram(p.getId());
            p.setUniformMat4("proj", scene.getCamera().getProjectionMatrix());
            p.setUniformMat4("view", scene.getCamera().getViewMatrix());


            var model = scene.getSelectedModel();

            model.draw(p);

            glStencilMask(0xFF);
            glEnable(GL_DEPTH_TEST);
            glStencilFunc(GL_ALWAYS, 1, 0xFF);
            glEnable(GL_DEPTH_TEST);
            glLineWidth(Settings.GLLineWidth);
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        }

        if(Settings.EnableMSAA.get()) {
            glBindFramebuffer(GL_READ_FRAMEBUFFER, scene.msFrameBuffer.getId());
            glBindFramebuffer(GL_DRAW_FRAMEBUFFER, scene.getFrameBuffer().getId());
            glBlitFramebuffer(0, 0, Constants.FRAMEBUFFER_WIDTH, Constants.FRAMEBUFFER_HEIGHT, 0, 0, Constants.FRAMEBUFFER_WIDTH, Constants.FRAMEBUFFER_HEIGHT, GL_COLOR_BUFFER_BIT, GL_NEAREST);
        }
        glBindFramebuffer(GL_FRAMEBUFFER, scene.selectFrameBuffer.getId());
        if(Settings.EnableLinePolygonMode.get()) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }
        if(Settings.EnableFaceCulling.get()) {
            glDisable(GL_CULL_FACE);
            glCullFace(GL_BACK);
        }

        glClearColor(0, 0, 0 , 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        var pr = ShaderProgramManager.getInstance().get("SELECT SHADER");
        glUseProgram(pr.getId());
        pr.setUniformMat4("view", scene.getCamera().getViewMatrix());
        pr.setUniformMat4("proj", scene.getCamera().getProjectionMatrix());
        scene.getModels().forEach((key, value) -> {
            pr.setUniformInt("id", value.getId());
            value.draw(pr);
        });

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    protected void renderImGui() {
        ImGui.showDemoWindow();
        gui.render();
    }

    @Override
    protected void onEnd() {
        ShaderProgramManager.getInstance().dispose();
        TextureManager.getInstance().dispose();
        ModelManager.getInstance().dispose();
        ShadowMap.getInstance().dispose();
        Scene.getInstance().dispose();
        SkyBox.getInstance().dispose();
        Grid.getInstance().dispose();
        Assets.getInstance().dispose();
    }
}
