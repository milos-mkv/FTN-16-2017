package gui.components;

import core.Scene;
import core.Settings;
import gui.Dock;
import imgui.ImGui;
import managers.TextureManager;

import static gui.GUIComponents.*;

public class ScenePropertiesDock implements Dock {

    private final Scene scene;

    public ScenePropertiesDock() {
        this.scene = Scene.getInstance();
    }

    @Override
    public void render() {
        if (!Settings.ShowScenePropertiesDock.get()) {
            return;
        }

        ImGui.begin("Scene Properties", Settings.ShowScenePropertiesDock);

        if (ImGui.collapsingHeader("Scene Items")) {
            scene.getModels().forEach((key, model) -> {
                ImGui.image(TextureManager.getInstance().getTexture("src/main/resources/images/mo.png").getId(), 20, 20);
                ImGui.sameLine();
                if (ImGui.treeNode(key)) {
                    scene.setSelectedModel(key);
                    model.getMeshes().forEach(mesh -> {
                        ImGui.image(TextureManager.getInstance().getTexture("src/main/resources/images/aaa.png").getId(), 20, 20);
                        ImGui.sameLine();
                        ImGui.selectable(mesh.getName());
                    });
                    ImGui.treePop();
                }
            });
        }
        if (ImGui.collapsingHeader("Clear Color")) {
            ImGui.colorPicker4("##ClearColor", Scene.ClearColor);
        }
        if (ImGui.collapsingHeader("Directional Light")) {
            controlDragFloat3("Direction", scene.getDirectionalLight().getDirection(), -1, 1);
            controlRGB("Ambient", scene.getDirectionalLight().getAmbient());
            controlRGB("Diffuse", scene.getDirectionalLight().getDiffuse());
            controlRGB("Specular", scene.getDirectionalLight().getSpecular());
        }

        ImGui.end();
    }

}
