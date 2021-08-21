package gui.components;

import core.MaterialPreviewScene;
import core.Scene;
import core.Settings;
import gfx.Material;
import gfx.ShaderProgram;
import gfx.Texture;
import gui.Dock;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import managers.ShaderProgramManager;
import managers.TextureManager;
import org.joml.Vector3f;
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
    private final ImInt selectedTextureType = new ImInt(0);
    private final String[] textureTypes;

    private final MaterialPreviewScene scene;
    private final TextureManager textureManager;

    public ModelPropertiesDock() {
        this.scene = MaterialPreviewScene.getInstance();
        this.textureManager = TextureManager.getInstance();
        this.textureTypes = new String[] { "Diffuse", "Specular", "Normal"};
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

            ImGui.combo("##Materials", selectedMaterialIndex, materialNameList);
            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 3.0f, 10.0f);
            ImGui.sameLine();
            if (ImGui.imageButton(textureManager.getTexture("src/main/resources/images/plus1.png").getId(), 21, 21)) {
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

        ImGui.setNextItemWidth(ImGui.getColumnWidth());

        ImGui.combo("##Textures", selectedTextureType,  textureTypes);
        ImGui.separator();

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 1.0f,3.0f);

        if(ImGui.imageButton(textureManager.getTexture("src/main/resources/images/folder.png").getId(), 30, 30)) {
            loadTextureForModel(material, TextureType.values()[selectedTextureType.get()]);
        }
        ImGui.sameLine();
        if(ImGui.imageButton(textureManager.getTexture("src/main/resources/images/gallery.png").getId(), 30, 30)) {
            Settings.ShowTexturePreviewDock.set(true);
            Texture t =  material.getTexture(TextureType.values()[selectedTextureType.get()]);
            if(t != null) {
                Settings.TextureForPreview = t.getPath();
            } else {
                Settings.TextureForPreview = null;
            }

        }
        ImGui.sameLine();
        if(ImGui.imageButton(textureManager.getTexture("src/main/resources/images/cancel.png").getId(), 30, 30)) {
            material.setTexture(TextureType.values()[selectedTextureType.get()], null);
        }

        renderTextureComponent(material, TextureType.values()[selectedTextureType.get()]);

        ImGui.popStyleVar();
    }

    private void renderTextureComponent( Material material, TextureType textureType) {
        Texture texture = material.getTexture(textureType);
        String path = texture == null ? "null" : texture.getPath();
        ImGui.setNextItemWidth(ImGui.getColumnWidth());
        ImGui.inputText("##Texture path", new ImString(path), ImGuiInputTextFlags.ReadOnly);
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

        ImGui.beginChildFrame(1, ImGui.getColumnWidth(), ImGui.getColumnWidth(), ImGuiWindowFlags.NoScrollbar);
        ImGui.image(scene.getFrameBuffer().getTexture(), ImGui.getColumnWidth(), ImGui.getColumnWidth(), 0, 1, 1, 0);
        ImGui.endChild();
    }
}
