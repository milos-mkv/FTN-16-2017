package core;

import gfx.*;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL32;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.util.nfd.NativeFileDialog;

import java.nio.ByteBuffer;

import static utils.Utils.ToFloat3;

public abstract class GUI {

    public static void init() {

    }

    public static void renderMenuBar(FrameBuffer fb) {
        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("New")) { }
                if (ImGui.menuItem("Open", "Ctrl+O")) { }
                ImGui.separator();
                if(ImGui.menuItem("Import Model")) {
                    try {
                        PointerBuffer pointerBuffer = PointerBuffer.allocateDirect(1);
                        NativeFileDialog.NFD_OpenDialog((CharSequence) null, null, pointerBuffer);
                        String path = pointerBuffer.getStringASCII();
                        Scene.loadModelFromFile(path.replace("\\", "/"));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                ImGui.separator();
                if (ImGui.menuItem("Exit", "Ctrl+Q")) {
                    GLFW.glfwSetWindowShouldClose(Window.handle, true);
                }
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Render")) {
                if (ImGui.menuItem("Render Image")) {
                    GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, fb.getId());
                    GL32.glPixelStorei(GL32.GL_PACK_ALIGNMENT, 1);
                    ByteBuffer data = BufferUtils.createByteBuffer(1280 * 796 * 3);
                    GL32.glReadPixels(0, 0, 1280, 769, GL32.GL_RGB, GL32.GL_UNSIGNED_BYTE, data);

                    STBImageWrite.nstbi_flip_vertically_on_write(1);
                    STBImageWrite.stbi_write_jpg("test.jpg", 1280, 769, 3, data, 1280 * 3);
                    GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, 0);
                }
                ImGui.endMenu();
            }
            ImGui.endMainMenuBar();
        }
    }

    public static void renderViewport(FrameBuffer fb, FirstPersonCameraController controller) {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.begin("Viewport", ImGuiWindowFlags.NoScrollbar);
        ImGui.image(fb.getTexture(), ImGui.getWindowSizeX(), ImGui.getWindowSizeY(), 0, 1, 1, 0);
        controller.aspect = ImGui.getWindowSize().x / ImGui.getWindowSize().y;
        ImGui.end();
        ImGui.popStyleVar();
    }

    public static void renderSceneItemsDock() {
        ImGui.begin("Scene Items");
        int i = 0;
        for(Model model : Scene.models) {

            if (ImGui.treeNode("Model: " + i)) {
//                Scene.selected = model;
                for(Mesh mesh : model.meshes) {
                    if(ImGui.selectable(mesh.name)) {
                        System.out.println(mesh.name);
                        Scene.selected = mesh;
                    };
                }
                ImGui.treePop();
            }
            i++;
        }

        ImGui.end();
    }

    public static void renderProperties() {
        ImGui.begin("Properties");
        if (ImGui.treeNode("Transform")) {
            if(Scene.selected != null) {
                renderDragFloat3("Position", Scene.selected.position);
                renderDragFloat3("Rotation", Scene.selected.rotation);
                renderDragFloat3("Scale", Scene.selected.scale);
            }
            ImGui.treePop();
        }
        if (ImGui.treeNode("Material")) {

            ImGui.treePop();
        }
        ImGui.end();
    }


    private static void renderDragFloat3(String text, Vector3f vec) {
        float[] buffer = ToFloat3(vec);
        ImGui.text(text);
        ImGui.dragFloat3("##"+text, buffer, 0.01f);
        vec.set(buffer);
    }

}
