package gui.components;

import core.Scene;
import core.Settings;
import gfx.Model;
import gui.Dock;
import imgui.ImGui;
import managers.TextureManager;
import org.lwjgl.glfw.GLFW;

import static gui.GUIControls.*;
import static org.lwjgl.opengl.GL11.*;

public class ScenePropertiesDock implements Dock {

    private final Scene scene;
    private final MeshPropertiesPopup meshPropertiesPopup;
    private String modelToRemove = null;
    private float[] lineWidth = new float[1];

    public ScenePropertiesDock() {
        this.scene = Scene.getInstance();
        this.meshPropertiesPopup = new MeshPropertiesPopup();
        this.lineWidth[0] = 1.0f;
    }

    @Override
    public void render() {
        if (!Settings.ShowScenePropertiesDock.get()) {
            return;
        }

        ImGui.begin("Scene Properties", Settings.ShowScenePropertiesDock);

        if (ImGui.collapsingHeader("Scene Items")) {
            scene.getModels().forEach((modelName, model) -> {
                ImGui.image(TextureManager.getInstance().getTexture("src/main/resources/images/mo.png").getId(), 20, 20);
                ImGui.sameLine();
                renderModelTreeNode(modelName, model);
            });
            if (modelToRemove != null) {
                scene.getModels().remove(modelToRemove);
                scene.setSelectedModel(null);
                modelToRemove = null;
            }
        }
        if (ImGui.collapsingHeader("Clear Color")) {
            ImGui.colorPicker4("##ClearColor", Scene.ClearColor);
        }
        if (ImGui.collapsingHeader("Directional Light")) {
            ImGui.checkbox("Enable Directional Light", Settings.EnableDirectionalLight);
            controlDragFloat3("Direction", scene.getDirectionalLight().getDirection(), -1, 1);
            controlRGB("Ambient", scene.getDirectionalLight().getAmbient());
            controlRGB("Diffuse", scene.getDirectionalLight().getDiffuse());
            controlRGB("Specular", scene.getDirectionalLight().getSpecular());
        }

        if (ImGui.collapsingHeader("OpenGL Functions")) {
            ImGui.checkbox("Enable shadows", Settings.EnableShadows);
            ImGui.checkbox("Enable line polygon mode", Settings.EnableLinePolygonMode);
            ImGui.checkbox("Enable face culling", Settings.EnableFaceCulling);
            ImGui.checkbox("Enable MSAA", Settings.EnableMSAA);
            ImGui.checkbox("Enable skybox", Settings.ToggleSkyBox);
            ImGui.checkbox("Enable grid", Settings.ToggleGrid);
            if(ImGui.checkbox("Cap FPS to 60", Settings.CapFPS)) {
                GLFW.glfwSwapInterval(Settings.CapFPS.get() ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
            }

            ImGui.text("Line width");
            ImGui.setNextItemWidth(ImGui.getColumnWidth());
            if (ImGui.dragFloat("##Line width", lineWidth, 0.1f, 0.0f, 10.f)) {
                glLineWidth(lineWidth[0]);
                Settings.GLLineWidth = lineWidth[0];
            }
        }

        ImGui.end();
    }

    private void renderModelTreeNode(String modelName, Model model) {
        var cur = ImGui.getCursorPos();

        if (ImGui.treeNode(modelName)) {
            scene.setSelectedModel(modelName);

            model.getMeshes().forEach((meshName, mesh) -> {
                ImGui.image(TextureManager.getInstance().getTexture("src/main/resources/images/aaa.png").getId(), 20, 20);
                ImGui.sameLine();
                if (ImGui.selectable(meshName)) {
                    scene.setSelectedMesh(meshName);
                    ImGui.openPopup("Mesh Properties");
                }
            });
            meshPropertiesPopup.render();
            ImGui.treePop();
        }
        var cur1 = ImGui.getCursorPos();

        ImGui.setCursorPos(ImGui.getWindowSizeX() - 33, cur.y - 2);
        if (ImGui.imageButton(TextureManager.getInstance().getTexture("src/main/resources/images/trash.png").getId(), 18, 18)) {
            modelToRemove = modelName;
        }

        ImGui.setCursorPos(cur1.x, cur1.y);
    }

}
