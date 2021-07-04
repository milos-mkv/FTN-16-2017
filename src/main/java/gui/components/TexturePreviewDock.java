package gui.components;

import core.Settings;
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

        ImGui.begin("Texture preview", Settings.ShowTexturePreviewDock, ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoCollapse);
        if(Settings.TextureInPreview != null) {
            ImGui.image(Settings.TextureInPreview.getId(), Settings.TextureInPreview.getWidth(), Settings.TextureInPreview.getHeight());
        }
        ImGui.end();
    }

}
