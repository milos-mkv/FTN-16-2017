package gui.components;

import core.Scene;
import core.Settings;
import gfx.Material;
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

        if(ImGui.collapsingHeader("Materials")) {
            model.getMaterials().forEach(this::renderMaterial);
        }

        ImGui.end();
    }

    private void renderMaterial(Material material) {
        if(ImGui.treeNode(material.getName())) {

            float3ControlRGB("Ambient Color", material.getAmbientColor(), 0, 1);
            float3ControlRGB("Diffuse Color", material.getDiffuseColor(), 0, 1);
            float3ControlRGB("Specular Color", material.getSpecularColor(), 0, 1);

            material.setShininess(floatControl("Shininess", material.getShininess()));
            material.setReflectance(floatControl("Reflectance", material.getReflectance()));

            displayTexture("Diffuse Texture", material.getDiffuseTexture());
            displayTexture("Specular Texture", material.getSpecularTexture());
            displayTexture("Normal Texture", material.getNormalTexture());

            ImGui.treePop();
        }
    }

}
