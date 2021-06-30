package gui.components;

import core.Scene;
import core.Settings;
import gui.Dock;
import imgui.ImGui;
import managers.TextureManager;

import static gui.GUIComponents.*;

public class ScenePropertiesDock implements Dock {

    @Override
    public void render() {
        if (!Settings.ShowScenePropertiesDock.get()) {
            return;
        }

        ImGui.begin("Scene Properties", Settings.ShowScenePropertiesDock);

        if (ImGui.collapsingHeader("Scene Items")) {
            Scene.getModels().forEach((key, model) -> {
                ImGui.image(TextureManager.getTexture("src/main/resources/images/rsd.png").getId(), 20, 20);
                ImGui.sameLine();
                if (ImGui.treeNode(key)) {
                    Scene.SelectedModel = key;
                    model.getMeshes().forEach(mesh -> {
                        ImGui.image(TextureManager.getTexture("src/main/resources/images/rsd.png").getId(), 20, 20);
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
//            float3ControlXYZ("Direction", Scene.getDirectionalLight().getDirection(), -1, 1);
            float3Control("Direction", Scene.getDirectionalLight().getDirection(), -1, 1);
            float3ControlRGB("Ambient", Scene.getDirectionalLight().getAmbient(),0, 1);
            float3ControlRGB("Diffuse", Scene.getDirectionalLight().getDiffuse(), 0, 1);
            float3ControlRGB("Specular", Scene.getDirectionalLight().getSpecular(), 0, 1);
        }

        ImGui.end();
    }

}