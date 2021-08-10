package gui.components;

import core.Scene;
import core.Settings;
import gfx.Model;
import gui.Dock;
import imgui.ImGui;
import managers.TextureManager;

import static gui.GUIControls.*;

public class ScenePropertiesDock implements Dock {

    private final Scene scene;
    private final MeshPropertiesPopup meshPropertiesPopup;
    private String modelToRemove = null;

    public ScenePropertiesDock() {
        this.scene = Scene.getInstance();
        this.meshPropertiesPopup = new MeshPropertiesPopup();
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
