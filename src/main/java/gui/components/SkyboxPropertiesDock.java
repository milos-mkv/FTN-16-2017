package gui.components;

import core.Settings;
import exceptions.InvalidDocumentException;
import gfx.CubeMap;
import gfx.SkyBox;
import gui.Dock;
import imgui.internal.ImGui;
import managers.TextureManager;

import java.util.List;

import static gui.GUIControls.controlOpenFileDialog;

public class SkyboxPropertiesDock implements Dock {

    private final SkyBox skyBox;
    private final List<String> list;

    public SkyboxPropertiesDock() {
        this.skyBox = SkyBox.getInstance();
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

        float size = 150;

        ImGui.columns(6);
        for (int i = 0; i < 6; i++)
            ImGui.setColumnWidth(i, 173);
        ImGui.text("Right image");
        if (ImGui.imageButton(TextureManager.getInstance().getTexture(skyBox.getCubemap().getFaces().get(0)).getId(),
                size, size)) {
            String path = controlOpenFileDialog();
            if(TextureManager.getInstance().getTexture(path) != null) {
                list.set(0, path);
            }

        }
        ImGui.nextColumn();
        ImGui.text("Left image");
        if (ImGui.imageButton(TextureManager.getInstance().getTexture(skyBox.getCubemap().getFaces().get(1)).getId(),
                size, size)) {
            String path = controlOpenFileDialog();
            if(TextureManager.getInstance().getTexture(path) != null) {
                list.set(1, path);
            }
        }
        ImGui.nextColumn();
        ImGui.text("Top image");
        if (ImGui.imageButton(TextureManager.getInstance().getTexture(skyBox.getCubemap().getFaces().get(2)).getId(),
                size, size)) {
            String path = controlOpenFileDialog();
            if(TextureManager.getInstance().getTexture(path) != null) {
                list.set(2, path);
            }
        }
        ImGui.nextColumn();
        ImGui.text("Bottom image");
        if (ImGui.imageButton(TextureManager.getInstance().getTexture(skyBox.getCubemap().getFaces().get(3)).getId(),
                size, size)) {
            String path = controlOpenFileDialog();
            if(TextureManager.getInstance().getTexture(path) != null) {
                list.set(3, path);
            }
        }
        ImGui.nextColumn();
        ImGui.text("Back image");
        if (ImGui.imageButton(TextureManager.getInstance().getTexture(skyBox.getCubemap().getFaces().get(4)).getId(),
                size, size)) {
            String path = controlOpenFileDialog();
            if(TextureManager.getInstance().getTexture(path) != null) {
                list.set(4, path);
            }
        }
        ImGui.nextColumn();
        ImGui.text("Front image");
        if (ImGui.imageButton(TextureManager.getInstance().getTexture(skyBox.getCubemap().getFaces().get(5)).getId(),
                size, size)) {
            String path = controlOpenFileDialog();
            if(TextureManager.getInstance().getTexture(path) != null) {
                list.set(5, path);
            }
        }
        ImGui.end();
    }

}
