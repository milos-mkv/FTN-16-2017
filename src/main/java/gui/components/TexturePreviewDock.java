package gui.components;

import core.Scene;
import core.Settings;
import gfx.Texture;
import gui.Dock;
import gui.GUIControls;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import managers.TextureManager;
import org.lwjgl.stb.STBImage;

import java.util.Arrays;

public class TexturePreviewDock implements Dock {

    private final ImInt selectedTextureIndex = new ImInt(0);
    private float zoom = 1.0f;
    private boolean flip = false;

    @Override
    public void render() {
        if(!Settings.ShowTexturePreviewDock.get()) {
            return;
        }
        ImGui.begin("Texture Preview", Settings.ShowTexturePreviewDock);
        String[] availabeTextures = TextureManager.getInstance()
                .getTextures()
                .keySet()
                .toArray(new String[0]);

        if (Settings.TextureForPreview != null) {
            for(int i=0;i<availabeTextures.length;i++) {
                if (availabeTextures[i].equals(Settings.TextureForPreview)) {
                    selectedTextureIndex.set(i);
                    break;
                }
            }
            Settings.TextureForPreview = null;
        }

        ImGui.text("Loaded textures:");
        ImGui.setNextItemWidth(ImGui.getColumnWidth());
        if(ImGui.combo("##Available texture", selectedTextureIndex, availabeTextures)) {
            zoom = 1.0f;
        }

        Texture texture = TextureManager.getInstance().getTexture(availabeTextures[selectedTextureIndex.get()]);

        ImGui.beginChildFrame(1, ImGui.getColumnWidth() , 300, ImGuiWindowFlags.HorizontalScrollbar);
        ImGui.image(texture.getId(), texture.getWidth() * zoom, texture.getHeight() * zoom);
        ImGui.endChildFrame();
        if(ImGui.button("Filp texture", ImGui.getColumnWidth(), 26)) {
            STBImage.stbi_set_flip_vertically_on_load(flip = !flip);
            Texture t = TextureManager.getInstance().getTexture(availabeTextures[selectedTextureIndex.get()]);
            t.dispose();
            t.setId(new Texture(availabeTextures[selectedTextureIndex.get()]).getId());
            STBImage.stbi_set_flip_vertically_on_load(false);
        }
        zoom = GUIControls.controlDragFloat("Zoom:", zoom, 0.001f);
        if(zoom <= 0) {
            zoom = 0.01f;
        }

        ImGui.text("Texture width: " + texture.getWidth());
        ImGui.text("Texture height: " + texture.getHeight());

        ImGui.end();
    }
}
