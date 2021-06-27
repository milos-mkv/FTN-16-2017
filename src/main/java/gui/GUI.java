package gui;

import core.Constants;
import core.Scene;
import core.Settings;
import core.Window;
import exceptions.InvalidDocumentException;
import gfx.Model;
import imgui.ImGui;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import managers.ErrorManager;
import managers.TextureManager;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.util.nfd.NativeFileDialog;

import java.util.logging.Level;
import java.util.logging.Logger;


import static gui.GUIComponents.errorDialog;
import static gui.GUIComponents.renderDragFloat3;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL30C.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30C.glBindFramebuffer;

public interface GUI {

     static void renderMainMenuBar() {
        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("Import Model")) {
                    openImportModelFailedDialog();
                }
                ImGui.separator();
                if (ImGui.menuItem("Exit", "Ctrl+Q")) {
                    GLFW.glfwSetWindowShouldClose(Window.getHandle(), true);
                }
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Render")) {
                if (ImGui.menuItem("Render Image")) {
                    executeRenderImage();
                }
                ImGui.endMenu();
            }
            ImGui.endMainMenuBar();
        }
    }

    public static void renderViewport() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);

        ImGui.begin("Viewport", ImGuiWindowFlags.NoScrollbar);
        ImGui.image(Scene.getFrameBuffer().getTexture(), ImGui.getWindowSizeX(), ImGui.getWindowSizeY(), 0, 1, 1, 0);
        Scene.getFPSCamera().setAspect(ImGui.getWindowSize().x / ImGui.getWindowSize().y);

        // ImGuizmo
        if (Scene.SelectedModel != null) {
            ImGuizmo.setOrthographic(false);
            ImGuizmo.setEnabled(true);
            ImGuizmo.setDrawList();
            float wwidht = ImGui.getWindowWidth();
            float wheight = ImGui.getWindowHeight();
            ImGuizmo.setRect(ImGui.getWindowPos().x, ImGui.getWindowPos().y, wwidht, wheight);
            var view = new float[16];
            Scene.getFPSCamera().getViewMatrix().get(view);
            var proj = new float[16];
            Scene.getFPSCamera().getProjectionMatrix().get(proj);
            var arr = new float[16];
            Scene.getModels().get(Scene.SelectedModel).getTransform().get(arr);

            var id = new float[16];
            new Matrix4f().set(id);
            ImGuizmo.drawGrid(view, proj, id, 100);

            ImGuizmo.manipulate(view, proj, arr, Operation.TRANSLATE, Mode.LOCAL);

            if (ImGuizmo.isUsing()) {
                Matrix4f calculatedMatrix = new Matrix4f().set(arr);
                var rot = new AxisAngle4f();
                calculatedMatrix.getRotation(rot);
                // TODO: ROTATION
                calculatedMatrix.getTranslation(Scene.getModels().get(Scene.SelectedModel).getPosition());
                calculatedMatrix.getScale(Scene.getModels().get(Scene.SelectedModel).getScale());
            }
        }


        ImGui.end();
        ImGui.popStyleVar();
    }

    public static void renderConsoleDock() {
        ImGui.begin("Console");
        ImGui.pushFont(Window.codeFont);
        for (String error : ErrorManager.getErrors())
            ImGui.text(error);
        ImGui.popFont();
        ImGui.end();
    }

    public static void renderModals() {
        if (Settings.OpenImportModelDialog) {
            ImGui.openPopup("Import Model Failed");
            Settings.OpenImportModelDialog = false;
        }
        errorDialog("Import Model Failed");
    }

     static void renderScenePropertiesDock() {
        if (!Settings.ShowSceneItemsDock.get()) {
            return;
        }
        
        ImGui.begin("Scene Properties");

        if (ImGui.collapsingHeader("Scene Items")) {
            Scene.getModels().forEach((key, value) -> {
                ImGui.image(TextureManager.getTexture("src/main/resources/images/3dd.png").getId(), 20, 20);
                ImGui.sameLine();
                if (ImGui.selectable(key)) {
                    Scene.SelectedModel = key;
                }
            });
        }
        if (ImGui.collapsingHeader("Directional Light")) {
            renderDragFloat3("Direction", Scene.getDirectionalLight().getDirection());
            renderDragFloat3("Ambient", Scene.getDirectionalLight().getAmbient(), 0, 1);
            renderDragFloat3("Diffuse", Scene.getDirectionalLight().getDirection(), 0, 1);
            renderDragFloat3("Specular", Scene.getDirectionalLight().getSpecular(), 0, 1);
        }
        if (ImGui.collapsingHeader("Clear Color")) {
            ImGui.colorPicker4("##ClearColor", Scene.ClearColor);
        }
        ImGui.end();
    }

    static void renderModelPropertiesDock() {
        ImGui.begin("Model Properties");

        var model = Scene.getModels().get(Scene.SelectedModel);

        if (ImGui.collapsingHeader("Transform Component")) {
            renderDragFloat3("Position", model.getPosition());
            renderDragFloat3("Rotation", model.getRotation());
            renderDragFloat3("Scale", model.getScale());
        }

        ImGui.end();
    }

    private static void openImportModelFailedDialog() {
        try {
            var pointerBuffer = PointerBuffer.allocateDirect(1);
            NativeFileDialog.NFD_OpenDialog((CharSequence) null, null, pointerBuffer);
            Scene.getModels().put("Model " + Scene.getModels().size(), new Model(pointerBuffer.getStringASCII().replace("\\", "/")));
        } catch (InvalidDocumentException e) {
            Settings.OpenImportModelDialog = true;
            ErrorManager.getErrors().add(e.getMessage());
            Logger.getGlobal().log(Level.WARNING, e.getMessage());
        } catch (Exception e) {
            Logger.getGlobal().log(Level.INFO, "Ignoring import model dialog!");
        }
    }

    private static void executeRenderImage() {
        glBindFramebuffer(GL_FRAMEBUFFER, Scene.getFrameBuffer().getId());
        glPixelStorei(GL_PACK_ALIGNMENT, 1);
        var data = BufferUtils.createByteBuffer(Constants.WINDOW_DEFAULT_WIDTH * Constants.WINDOW_DEFAULT_HEIGHT * 3);
        glReadPixels(0, 0, Constants.WINDOW_DEFAULT_WIDTH, Constants.WINDOW_DEFAULT_HEIGHT, GL_RGB, GL_UNSIGNED_BYTE, data);

        STBImageWrite.nstbi_flip_vertically_on_write(1);
        STBImageWrite.stbi_write_jpg("test.jpg", Constants.WINDOW_DEFAULT_WIDTH, Constants.WINDOW_DEFAULT_HEIGHT, 3, data, Constants.WINDOW_DEFAULT_WIDTH * 3);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    static void render() {
        renderMainMenuBar();
        renderViewport();
        renderModals();
        renderConsoleDock();
        renderScenePropertiesDock();
        renderModelPropertiesDock();
    }
}
