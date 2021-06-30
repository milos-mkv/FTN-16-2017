package gui.components;

import core.Scene;
import core.Settings;
import gui.Dock;
import imgui.ImGui;

import static gui.GUIComponents.*;

public class ModelPropertiesDock implements Dock {

    @Override
    public void render() {
        if(!Settings.ShowModelPropertiesDock.get()) {
            return;
        }

        ImGui.begin("Model Properties", Settings.ShowModelPropertiesDock);

        var model = Scene.getModels().get(Scene.SelectedModel);

        if (ImGui.collapsingHeader("Transform Component")) {
            float3Control("Position", model.getPosition(), 0, 0);
            float3Control("Rotation", model.getRotation(), 0, 0);
            float3Control("Scale", model.getScale(), 0, 0);
        }

        ImGui.end();
    }

}
