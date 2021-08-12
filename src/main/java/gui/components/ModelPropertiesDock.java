package gui.components;

import core.MaterialPreviewScene;
import core.Scene;
import core.Settings;
import gfx.FrameBuffer;
import gfx.Material;
import gfx.ShaderProgram;
import gfx.Texture;
import gui.Dock;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import managers.ShaderProgramManager;
import managers.TextureManager;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;
import utils.TextureType;

import static gui.GUIControls.*;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class ModelPropertiesDock implements Dock {

    private final ImString newMaterialName = new ImString();
    private final ImInt selectedMaterialIndex = new ImInt(0);

    MaterialPreviewScene scene;

    public ModelPropertiesDock() {
        scene = MaterialPreviewScene.getInstance();
    }

    @Override
    public void render() {
        if (!Settings.ShowModelPropertiesDock.get()) {
            return;
        }
        var model = Scene.getInstance().getSelectedModel();

        if (model == null) {
            return;
        }

        ImGui.begin("Model Properties", Settings.ShowModelPropertiesDock);

        if (ImGui.collapsingHeader("Transform Component")) {
            controlDragFloat3("Position", model.getPosition(), 0, 0);
            controlDragFloat3("Rotation", model.getRotation(), 0, 0);
            controlDragFloat3("Scale", model.getScale(), 0, 0);
        }

        if (ImGui.collapsingHeader("Materials")) {
            ImGui.text("Available materials");
            ImGui.setNextItemWidth(ImGui.getColumnWidth() - 30);
            String[] materialNameList = model.getMaterials().keySet().toArray(new String[0]);

            if(selectedMaterialIndex.get() >= materialNameList.length) {
                selectedMaterialIndex.set(0);
            }

            ImGui.combo("##Materialss", selectedMaterialIndex, materialNameList);
            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 3.0f, 10.0f);
            ImGui.sameLine();
            if (ImGui.imageButton(TextureManager.getInstance().getTexture("src/main/resources/images/plus1.png").getId(), 21, 21)) {
                ImGui.openPopup("Add new material");
                newMaterialName.set("");
            }

            if (ImGui.beginPopupModal("Add new material", new ImBoolean(true), ImGuiWindowFlags.NoResize)) {
                ImGui.setWindowSize(300, 102);
                ImGui.text("New material name");
                ImGui.setNextItemWidth(ImGui.getColumnWidth() - 40);
                ImGui.inputText("##name", newMaterialName);
                ImGui.sameLine();
                if (ImGui.button("Add")) {
                    model.getMaterials().computeIfAbsent(newMaterialName.get(), Material::new);
                    ImGui.closeCurrentPopup();
                }
                ImGui.endPopup();
            }
            ImGui.popStyleVar();
            ImGui.separator();
            renderMaterial(model.getMaterials().get(materialNameList[selectedMaterialIndex.get()]));
        }

        ImGui.end();
    }

    private void renderMaterial(Material material) {
        controlRGB("Ambient Color", material.getAmbientColor());
        controlRGB("Diffuse Color", material.getDiffuseColor());
        controlRGB("Specular Color", material.getSpecularColor());

        material.setShininess(controlDragFloat("Shininess", material.getShininess(), 0.1f));

        ImGui.text("Material preview");
        renderMaterialPreview(material);
        ImGui.text("Textures");

        renderTextureComponent("Diffuse Texture", material, TextureType.DIFFUSE);
        renderTextureComponent("Specular Texture", material, TextureType.SPECULAR);
        renderTextureComponent("Normal Texture", material, TextureType.NORMAL);
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
        if (texture == null) {
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

    private void renderMaterialPreview(Material material) {

        glViewport(0, 0, 600, 600);
        glBindFramebuffer(GL_FRAMEBUFFER, scene.getFrameBuffer().getId());
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        ShaderProgram program = ShaderProgramManager.getInstance().get("MATERIAL PREVIEW SHADER");
        scene.getSphere().getMeshes().forEach((key, value) -> value.setMaterial(material));
        glUseProgram(program.getId());
        program.setUniformVec3("viewPos", scene.getPerspectiveCamera().getPosition());
        Scene.getInstance().getDirectionalLight().apply(program);

        program.setUniformMat4("view", scene.getPerspectiveCamera().getViewMatrix());
        program.setUniformMat4("proj", scene.getPerspectiveCamera().getProjectionMatrix());

        program.setUniformInt("diffuseTexture", 1);
        scene.getSphere().setRotationAngle((float)Math.toRadians(glfwGetTime() * 30.f));
        scene.getSphere().setRotation(new Vector3f(0, 1, 0));
        scene.getSphere().draw(program);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        ImGui.beginChildFrame(1, ImGui.getWindowWidth() - 30, ImGui.getWindowWidth()-30, ImGuiWindowFlags.NoScrollbar);
        ImGui.image(scene.getFrameBuffer().getTexture(), ImGui.getColumnWidth(), ImGui.getColumnWidth(), 0, 1, 1, 0);
        ImGui.endChild();
    }
}
