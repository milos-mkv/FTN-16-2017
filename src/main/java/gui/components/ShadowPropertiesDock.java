package gui.components;

import core.Settings;
import gfx.ShadowMap;
import gui.Dock;
import imgui.ImGui;

public class ShadowPropertiesDock implements Dock {


    @Override
    public void render() {
        if(!Settings.ShowShadowPropertiesDock.get()) {
            return;
        }
        ImGui.begin("Shadow Properties", Settings.ShowShadowPropertiesDock);
        ImGui.checkbox("Enable Shadows", Settings.ToogleShadows);
        if(ImGui.collapsingHeader("Shadow Map")) {
            ImGui.image(ShadowMap.getDepthMap(), 300, 300, 0, 1, 1, 0);
        }
        ImGui.end();
    }
}
