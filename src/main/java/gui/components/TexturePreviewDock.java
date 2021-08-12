package gui.components;

import core.Settings;
import gfx.Texture;
import gui.Dock;
import gui.GUIControls;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import managers.TextureManager;

public class TexturePreviewDock implements Dock {

    private final ImInt selectedTextureIndex = new ImInt(0);
    private float zoomX = 0;
    private float zoomY = 0;

    @Override
    public void render() {
        if(!Settings.ShowTexturePreviewDock.get()) {
            return;
        }
        ImGui.begin("Texture Preview", Settings.ShowTexturePreviewDock);
        String[] availabeTextures = TextureManager.getInstance().getTextures().keySet().toArray(new String[0]);
        ImGui.text("Loaded textures:");
        ImGui.setNextItemWidth(ImGui.getColumnWidth());
        ImGui.combo("##Available texture", selectedTextureIndex, availabeTextures);

        Texture texture = TextureManager.getInstance().getTexture(availabeTextures[selectedTextureIndex.get()]);

        ImGui.beginChildFrame(1, ImGui.getColumnWidth() , ImGui.getColumnWidth(), ImGuiWindowFlags.HorizontalScrollbar);
        ImGui.image(texture.getId(), texture.getWidth() + zoomX, texture.getHeight() + zoomY);
        ImGui.endChildFrame();
        ImGui.columns(2);
        zoomX = GUIControls.controlDragFloat("Zoom X:", zoomX);
        ImGui.nextColumn();
        zoomY = GUIControls.controlDragFloat("Zoom Y:", zoomY);
        ImGui.nextColumn();
        ImGui.columns(1);
        ImGui.text("Texture width: " + texture.getWidth());
        ImGui.text("Texture height: " + texture.getHeight());
        ImGui.end();
    }
}
