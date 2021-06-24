package gui;

import core.Constants;
import core.Scene;
import core.Settings;
import core.Window;
import gfx.Model;
import imgui.ImGui;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import managers.TextureManager;
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
    private static final float[] INPUT_BOUNDS = new float[]{-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f};
    private static final float[] INPUT_BOUNDS_SNAP = new float[]{1f, 1f, 1f};
    private static final float[] INPUT_SNAP_VALUE = new float[]{1f, 1f, 1f};
    public static void renderViewport() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.begin("Viewport", ImGuiWindowFlags.NoScrollbar);
        ImGui.image(Scene.getFrameBuffer().getTexture(), ImGui.getWindowSizeX(), ImGui.getWindowSizeY(), 0, 1, 1, 0);
        Scene.getFPSCamera().aspect = ImGui.getWindowSize().x / ImGui.getWindowSize().y;

        // ImGuizmo
        if(Scene.SelectedModel !=null) {
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

            ImGuizmo.manipulate(view, proj, arr , Operation.TRANSLATE, Mode.WORLD);

            if(ImGuizmo.isUsing()) {
                float[] pos = { arr[12], arr[13], arr[14] };

                Scene.getModels().get(Scene.SelectedModel).position.set(pos);
            }
        }



        ImGui.end();
        ImGui.popStyleVar();
    }

    public static void renderScenePropertiesDock() {
        if(!Settings.ShowSceneItemsDock.get()) {
            return;
        }
        ImGui.begin("Scene Properties");
        if(ImGui.collapsingHeader("Scene Items")) {
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

        if (ImGui.collapsingHeader("Transform Component") && Scene.SelectedModel != null) {
            renderDragFloat3("Position", Scene.getModels().get(Scene.SelectedModel).position);
            renderDragFloat3("Rotation", Scene.getModels().get(Scene.SelectedModel).rotation);
            renderDragFloat3("Scale",    Scene.getModels().get(Scene.SelectedModel).scale);
        }
        ImGui.end();
    }


}
