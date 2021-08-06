package gui.components;

import core.Scene;
import core.Settings;
import gfx.Material;
import gfx.Texture;
import gui.Dock;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import managers.TextureManager;
import utils.TextureType;

import static gui.GUIControls.*;

public class ModelPropertiesDock implements Dock {

    @Override
    public void render() {
        if (!Settings.ShowModelPropertiesDock.get()) {
            return;
        }

        ImGui.begin("Model Properties", Settings.ShowModelPropertiesDock);
        var model = Scene.getInstance().getSelectedModel();

        if (model != null && ImGui.collapsingHeader("Transform Component")) {
            controlDragFloat3("Position", model.getPosition(), 0, 0);
            controlDragFloat3("Rotation", model.getRotation(), 0, 0);
            controlDragFloat3("Scale", model.getScale(), 0, 0);
        }

        if (model != null && ImGui.collapsingHeader("Materials")) {
            model.getMaterials().forEach(this::renderMaterial);
        }

        ImGui.end();
    }

    private void renderMaterial(Material material) {
        if (ImGui.treeNode(material.getName())) {

            controlRGB("Ambient Color", material.getAmbientColor());
            controlRGB("Diffuse Color", material.getDiffuseColor());
            controlRGB("Specular Color", material.getSpecularColor());

            material.setShininess(controlDragFloat("Shininess", material.getShininess()));
            material.setReflectance(controlDragFloat("Reflectance", material.getReflectance()));

            ImGui.text("Textures");

            renderTextureComponent("Diffuse Texture", material, TextureType.DIFFUSE);
            renderTextureComponent("Specular Texture", material, TextureType.SPECULAR);
            renderTextureComponent("Normal Texture", material, TextureType.NORMAL);

            ImGui.treePop();
        }
    }

    private void renderTextureComponent(String label, Material material, TextureType textureType) {
        ImGui.pushStyleVar(ImGuiStyleVar.IndentSpacing, 0.0f);
        Texture texture = null;
        switch (textureType) {
            case DIFFUSE:
                texture = material.getDiffuseTexture();
                break;
            case SPECULAR:
                texture = material.getSpecularTexture();
                break;
            case NORMAL:
                texture = material.getNormalTexture();
                break;
            default:
                break;
        }
        if (texture == null) {
            if (ImGui.button("Load " + label, ImGui.getColumnWidth(), 26)) {
                loadTextureForModel(material, textureType);
            }
        } else if (ImGui.treeNode(label)) {
            ImGui.textDisabled(texture.getPath());
            ImGui.image(texture.getId(), 300, 300);
            if (ImGui.button("Change " + label, 300, 26)) {
                loadTextureForModel(material, textureType);
            }
            ImGui.treePop();
        }
        ImGui.popStyleVar();

    }

    private void loadTextureForModel(Material material, TextureType textureType) {
        String path = controlOpenFileDialog();
        if (path == null) {
            return;
        }
        var texture = TextureManager.getInstance().getTexture(path);
        if(texture == null) {
            return;
        }
        switch (textureType) {
            case DIFFUSE:
                material.setDiffuseTexture(texture);
                break;
            case SPECULAR:
                material.setSpecularTexture(texture);
                break;
            case NORMAL:
                material.setNormalTexture(texture);
                break;
            default:
                break;
        }
    }
}
