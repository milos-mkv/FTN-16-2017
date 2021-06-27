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
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import lombok.extern.java.Log;
import managers.ErrorManager;
import managers.TextureManager;
import org.joml.AxisAngle4f;
import org.joml.Matrix2d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL32;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.util.nfd.NativeFileDialog;


import java.nio.ByteBuffer;


import static gui.GUIComponents.errorDialog;
import static gui.GUIComponents.renderDragFloat3;


public abstract class GUI {


    public static void initialize() {
    }

    public static void renderMainMenuBar() {
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
        Scene.getFPSCamera().aspect = ImGui.getWindowSize().x / ImGui.getWindowSize().y;

        // ImGuizmo
        if (Scene.SelectedModel != null) {
            ImGuizmo.setOrthographic(false);
            ImGuizmo.setEnabled(true);
            ImGuizmo.setDrawList();
            float wwidht = ImGui.getWindowWidth();
            float wheight = ImGui.getWindowHeight();
            ImGuizmo.setRect(ImGui.getWindowPos().x, ImGui.getWindowPos().y, wwidht, wheight);
            float[] view = new float[16];
            Scene.getFPSCamera().getViewMatrix().get(view);
            float[] proj = new float[16];
            Scene.getFPSCamera().getProjectionMatrix().get(proj);
            float[] arr = new float[16];
            Scene.getModels().get(Scene.SelectedModel).getTransform().get(arr);

            float[] id = new float[16];
            new Matrix4f().set(id);
            ImGuizmo.drawGrid(view, proj, id, 100);

            ImGuizmo.manipulate(view, proj, arr, Operation.TRANSLATE, Mode.LOCAL);

            if (ImGuizmo.isUsing()) {
                Matrix4f calculatedMatrix = new Matrix4f().set(arr);
                AxisAngle4f rot = new AxisAngle4f();
                calculatedMatrix.getRotation(rot);
                System.out.println(rot.angle);
//                    Scene.getModels().get(Scene.SelectedModel).rotation.x = rot.angle * rot.x;
//
//                    Scene.getModels().get(Scene.SelectedModel).rotation.y = rot.angle * rot.y;
//
//                    Scene.getModels().get(Scene.SelectedModel).rotation.z = rot.angle * rot.z;
                calculatedMatrix.getTranslation(Scene.getModels().get(Scene.SelectedModel).position);
                calculatedMatrix.getScale(Scene.getModels().get(Scene.SelectedModel).scale);
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

    public static void renderScenePropertiesDock() {
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
            renderDragFloat3("Direction", Scene.getDirectionalLight().direction);
            renderDragFloat3("Ambient", Scene.getDirectionalLight().ambient, 0, 1);
            renderDragFloat3("Diffuse", Scene.getDirectionalLight().diffuse, 0, 1);
            renderDragFloat3("Specular", Scene.getDirectionalLight().specular, 0, 1);
        }
        if (ImGui.collapsingHeader("Clear Color")) {
            ImGui.colorPicker4("##ClearColor", Scene.ClearColor);
        }
        ImGui.end();
    }

    public static void renderModelPropertiesDock() {
        ImGui.begin("Model Properties");

        Model model = Scene.getModels().get(Scene.SelectedModel);

        if (ImGui.collapsingHeader("Transform Component")) {
            renderDragFloat3("Position", model.position);
            renderDragFloat3("Rotation", model.rotation);
            renderDragFloat3("Scale", model.scale);
        }

//        if(ImGui.collapsingHeader("Material")) {
//            renderDragFloat3("Ambient Color", model.m);
//            renderDragFloat3("Diffuse Color", Scene.getModels().get(Scene.SelectedModel).rotation);
//            renderDragFloat3("Specular Color", Scene.getModels().get(Scene.SelectedModel).scale);
//        }

        ImGui.end();
    }

    private static void openImportModelFailedDialog() {
        try {
            PointerBuffer pointerBuffer = PointerBuffer.allocateDirect(1);
            NativeFileDialog.NFD_OpenDialog((CharSequence) null, null, pointerBuffer);
            Scene.getModels().put("Model " + Scene.getModels().size(), new Model(pointerBuffer.getStringASCII().replace("\\", "/")));
        } catch (InvalidDocumentException e) {
            Settings.OpenImportModelDialog = true;
            ErrorManager.getErrors().add(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Ignoring import model dialog!");
        }
    }

    private static void executeRenderImage() {
        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, Scene.getFrameBuffer().getId());
        GL32.glPixelStorei(GL32.GL_PACK_ALIGNMENT, 1);
        ByteBuffer data = BufferUtils.createByteBuffer(Constants.WINDOW_DEFAULT_WIDTH * Constants.WINDOW_DEFAULT_HEIGHT * 3);
        GL32.glReadPixels(0, 0, Constants.WINDOW_DEFAULT_WIDTH, Constants.WINDOW_DEFAULT_HEIGHT, GL32.GL_RGB, GL32.GL_UNSIGNED_BYTE, data);

        STBImageWrite.nstbi_flip_vertically_on_write(1);
        STBImageWrite.stbi_write_jpg("test.jpg", Constants.WINDOW_DEFAULT_WIDTH, Constants.WINDOW_DEFAULT_HEIGHT, 3, data, Constants.WINDOW_DEFAULT_WIDTH * 3);
        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, 0);
    }

}
