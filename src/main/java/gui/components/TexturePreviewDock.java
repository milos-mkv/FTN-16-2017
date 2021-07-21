package gui.components;

import core.Settings;
import gfx.ShadowMap;
import gui.Dock;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public class TexturePreviewDock implements Dock {

    public TexturePreviewDock() { }

    @Override
    public void render() {
        if(!Settings.ShowTexturePreviewDock.get()) {
            return;
        }

        ImGui.begin("Texture preview", Settings.ShowTexturePreviewDock);
            ImGui.image(ShadowMap.getDepthMap(), 300, 300, 0, 1, 1, 0);
        ImGui.end();
    }

}
