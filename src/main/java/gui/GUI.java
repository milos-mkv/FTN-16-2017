package gui;

import core.Constants;
import core.Scene;
import core.Settings;
import core.Window;
import gfx.Model;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL32;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.util.nfd.NativeFileDialog;

import java.nio.ByteBuffer;

import static gui.GUIComponents.renderDragFloat3;


public abstract class GUI {

    public static void initialize() {

    }

    public static void renderMenuBar() {
        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("New")) {
                }
                if (ImGui.menuItem("Open", "Ctrl+O")) {
                }
                ImGui.separator();
                if (ImGui.menuItem("Import Model")) {
                    try {
                        PointerBuffer pointerBuffer = PointerBuffer.allocateDirect(1);
                        NativeFileDialog.NFD_OpenDialog((CharSequence) null, null, pointerBuffer);
                        String path = pointerBuffer.getStringASCII();
                        Scene.getModels().put("Model " + Scene.getModels().size(), new Model(path.replace("\\", "/")));
//                        Scene.loadModelFromFile(path.replace("\\", "/"));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }

                ImGui.separator();
                if (ImGui.menuItem("Exit", "Ctrl+Q")) {
                    GLFW.glfwSetWindowShouldClose(Window.getHandle(), true);
                }
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("View")) {
                if (ImGui.menuItem("Show/Hide Light Properties")) {
                    Settings.ShowLightPropertiesDock.set(!Settings.ShowLightPropertiesDock.get());
                }
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Render")) {
                if (ImGui.menuItem("Render Image")) {
                    GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, Scene.getFrameBuffer().getId());
                    GL32.glPixelStorei(GL32.GL_PACK_ALIGNMENT, 1);
                    ByteBuffer data = BufferUtils.createByteBuffer(Constants.WINDOW_DEFAULT_WIDTH * Constants.WINDOW_DEFAULT_HEIGHT * 3);
                    GL32.glReadPixels(0, 0, Constants.WINDOW_DEFAULT_WIDTH, Constants.WINDOW_DEFAULT_HEIGHT, GL32.GL_RGB, GL32.GL_UNSIGNED_BYTE, data);

                    STBImageWrite.nstbi_flip_vertically_on_write(1);
                    STBImageWrite.stbi_write_jpg("test.jpg", Constants.WINDOW_DEFAULT_WIDTH, Constants.WINDOW_DEFAULT_HEIGHT, 3, data, Constants.WINDOW_DEFAULT_WIDTH * 3);
                    GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, 0);
                }
                if (ImGui.menuItem("Enable/Disable Grid")) {
                    Settings.EnableGrid = !Settings.EnableGrid;
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
        ImGui.end();
        ImGui.popStyleVar();
    }

    public static void renderSceneItemsDock() {
        if(!Settings.ShowSceneItemsDock.get()) {
            return;
        }
        ImGui.begin("Scene Items");
        if(ImGui.treeNode("Scene Collection")) {
            Scene.getModels().forEach((key, value) -> {
                if (ImGui.selectable(key)) {
                    System.out.println(key);
                }
            });
            ImGui.treePop();
        }
        ImGui.end();
    }

    public static void renderLightProperties() {
        if(!Settings.ShowLightPropertiesDock.get()) {
            return;
        }
        ImGui.begin("Light Properties", Settings.ShowLightPropertiesDock);
        if (ImGui.treeNode("Directional Light")) {
            renderDragFloat3("Direction", Scene.getDirectionalLight().direction);
            renderDragFloat3("Ambient", Scene.getDirectionalLight().ambient, 0, 1);
            renderDragFloat3("Diffuse", Scene.getDirectionalLight().diffuse, 0, 1);
            renderDragFloat3("Specular", Scene.getDirectionalLight().specular, 0, 1);
            ImGui.treePop();
        }
        ImGui.end();
    }

}
