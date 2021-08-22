package gui.components;

import core.Settings;
import exceptions.InvalidDocumentException;
import gfx.CubeMap;
import gfx.SkyBox;
import gui.Dock;
import imgui.ImGui;
import imgui.ImVec2;
import managers.TextureManager;

import java.util.List;

import static gui.GUIControls.controlOpenFileDialog;

public class SkyboxPropertiesDock implements Dock {

    private final SkyBox skyBox;
    private final List<String> list;
    private final TextureManager textureManager;

    public SkyboxPropertiesDock() {
        this.skyBox = SkyBox.getInstance();
        this.textureManager = TextureManager.getInstance();
        this.list = this.skyBox.getCubemap().getFaces();
    }

    @Override
    public void render() {
        if (!Settings.ShowSkyboxPropertiesDock.get()) {
            return;
        }

        ImGui.begin("Skybox properties", Settings.ShowSkyboxPropertiesDock);
        if (ImGui.button("Update skybox")) {
            try {
                CubeMap cubeMap = new CubeMap(list);
                skyBox.getCubemap().dispose();
                skyBox.setCubemap(cubeMap);
            } catch (InvalidDocumentException e) {
                e.printStackTrace();
            }
        }
        ImGui.sameLine();
        ImGui.text("Make sure all images have same resolution.");

        for (int i = 0; i < 6; i++) {
            if (ImGui.imageButton(textureManager.getTexture(skyBox.getCubemap().getFaces().get(i)).getId(), 150, 150)) {
                String path = controlOpenFileDialog();
                if(textureManager.getTexture(path) != null) {
                    list.set(i, path);
                }
            }
            ImGui.sameLine();
        }
        ImGui.end();
    }

}
