package gui.components;

import core.Scene;
import core.Settings;
import gfx.Material;
import gfx.Texture;
import gui.Dock;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;

import static gui.GUIComponents.*;

public class ModelPropertiesDock implements Dock {

    @Override
    public void render() {
        if(!Settings.ShowModelPropertiesDock.get()) {
            return;
        }

        ImGui.begin("Model Properties", Settings.ShowModelPropertiesDock);
        var model = Scene.getSelectedModel();

        if (model != null && ImGui.collapsingHeader("Transform Component")) {
            controlDragFloat3("Position", model.getPosition(),  0, 0);
            controlDragFloat3("Rotation", model.getRotation(),  0, 0);
            controlDragFloat3("Scale",    model.getScale(),     0, 0);
        }

        if(model != null && ImGui.collapsingHeader("Materials")) {
            model.getMaterials().forEach(this::renderMaterial);
        }

        ImGui.end();
    }

    private void renderMaterial(Material material) {
        if(ImGui.treeNode(material.getName())) {

            controlRGB("Ambient Color", material.getAmbientColor());
            controlRGB("Diffuse Color", material.getDiffuseColor());
            controlRGB("Specular Color", material.getSpecularColor());

            material.setShininess(controlDragFloat("Shininess", material.getShininess()));
            material.setReflectance(controlDragFloat("Reflectance", material.getReflectance()));

            renderTextureComponent("Diffuse Texture", material.getDiffuseTexture());
            renderTextureComponent("Specular Texture", material.getSpecularTexture());
            renderTextureComponent("Normal Texture", material.getNormalTexture());

            ImGui.treePop();
        }
    }

    private void renderTextureComponent(String label, Texture texture) {
        ImGui.pushStyleVar(ImGuiStyleVar.IndentSpacing, 0.0f);
        if(texture == null) {
            if(ImGui.button("Load " + label, ImGui.getColumnWidth(), 26)) {
                // TODO
            }
        }
        else if(ImGui.treeNode(label)) {
            ImGui.textDisabled(texture.getPath());
            ImGui.image(texture.getId(), 300, 300);
            if(ImGui.button("Change " + label, 300, 26)) {
                // TODO
            }
            ImGui.treePop();
        }
        ImGui.popStyleVar();

    }

}
