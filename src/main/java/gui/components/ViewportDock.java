package gui.components;

import core.Constants;
import core.Scene;
import core.Settings;
import core.Window;
import gfx.Model;
import gfx.Texture;
import gui.Dock;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.*;
import lombok.SneakyThrows;
import managers.Console;
import managers.ModelManager;
import managers.TextureManager;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL30.*;
import static utils.Utils.map;
import static utils.Utils.matrix4x4ToFloatBuffer;

public class ViewportDock implements Dock {

    private final Scene scene;
    private final DecimalFormat df = new DecimalFormat();

    public ViewportDock() {
        this.scene = Scene.getInstance();
        df.setMaximumFractionDigits(2);
    }

    @Override
    public synchronized void render() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.begin("Viewport", ImGuiWindowFlags.NoScrollbar);

        var viewportOffset = ImGui.getCursorPos();

        ImGui.image(scene.getFrameBuffer().getTexture(), ImGui.getWindowSizeX(), ImGui.getWindowSizeY() - 30, 0, 1, 1, 0);

        var windowSize = ImGui.getWindowSize();
        var minBound = ImGui.getWindowPos();

        minBound.x += viewportOffset.x;
        minBound.y += viewportOffset.y;

        var maxBound = new ImVec2(minBound.x + windowSize.x, minBound.y + windowSize.y);
        ImVec2[] vbounds = new ImVec2[2];
        vbounds[0] = new ImVec2(minBound.x, minBound.y);
        vbounds[1] = new ImVec2(maxBound.x, maxBound.y);

        var m = ImGui.getMousePos();
        m.x -= vbounds[0].x;
        m.y -= vbounds[1].y - 27;
        m.y *= -1;

        float finalX = map(m.x, 0, vbounds[1].x - vbounds[0].x, 0, Constants.FRAMEBUFFER_WIDTH);
        float finalY = map(m.y, 0, vbounds[1].y - vbounds[0].y, 0, Constants.FRAMEBUFFER_HEIGHT);

        if (GLFW.glfwGetMouseButton(Window.getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
            glBindFramebuffer(GL_FRAMEBUFFER, Scene.getInstance().selectFrameBuffer.getId());
            int[] i = new int[1];
            glReadPixels( (int)finalX,
                    (int)finalY,
                    1, 1, GL_RED_INTEGER, GL_INT, i);

            for(Map.Entry<String, Model> mas : Scene.getInstance().getModels().entrySet()) {
                if(mas.getValue().getId() == i[0]) {
                    Scene.getInstance().setSelectedModel(mas.getKey());
                    break;
                }
            }
            glBindFramebuffer(GL_FRAMEBUFFER, 0);

        }


        scene.getCamera().setAspect(ImGui.getWindowSize().x / ImGui.getWindowSize().y);

        manipulate();

        var size = 20;

        ImGui.setCursorPos(10, 40);
        if (ImGui.imageButton(Objects.requireNonNull(TextureManager.getInstance().getTexture(Constants.ICON_TRANSLATE)).getId(),
                size, size)) {
            Settings.CurrentGizmoMode = Operation.TRANSLATE;
            Console.log(Console.Level.INFO, "Selected Translate Mode");
        }

        ImGui.setCursorPos(10, 70);
        if (ImGui.imageButton(Objects.requireNonNull(TextureManager.getInstance().getTexture(Constants.ICON_SCALE)).getId(),
                size, size)) {
            Settings.CurrentGizmoMode = Operation.SCALE;
            Console.log(Console.Level.INFO, "Selected Scale Mode");
        }

        ImGui.setCursorPos(10, 100);
        if (ImGui.imageButton(Objects.requireNonNull(TextureManager.getInstance().getTexture(Constants.ICON_ROTATE)).getId(),
                size, size)) {
            Settings.CurrentGizmoMode = Operation.ROTATE;
            Console.log(Console.Level.INFO, "Selected Rotate Mode");
        }
        ImGui.popStyleVar();

        if (ImGui.beginPopupContextWindow(ImGuiMouseButton.Middle)) {
            openContextMenu();
        }

        ImGui.setCursorPos(ImGui.getWindowSizeX() - 100, 30);

        ImGui.text("FPS: " + df.format(ImGui.getIO().getFramerate()));

        ImGui.end();
    }

    private void manipulate() {
        if (scene.getSelectedModel() == null) {
            return;
        }

        ImGuizmo.setOrthographic(false);
        ImGuizmo.setEnabled(true);
        ImGuizmo.setDrawList();
        ImGuizmo.setRect(ImGui.getWindowPosX(), ImGui.getWindowPosY(), ImGui.getWindowWidth(), ImGui.getWindowHeight());

        var model = scene.getSelectedModel();
        var view = matrix4x4ToFloatBuffer(scene.getCamera().getViewMatrix());
        var proj = matrix4x4ToFloatBuffer(scene.getCamera().getProjectionMatrix());
        var transform = matrix4x4ToFloatBuffer(model.getTransform());

        ImGuizmo.manipulate(view, proj, transform, Settings.CurrentGizmoMode, Mode.WORLD);

        if (ImGuizmo.isUsing()) {
            Matrix4f calculatedMatrix = new Matrix4f().set(transform);
            switch (Settings.CurrentGizmoMode) {
                case Operation.TRANSLATE:
                    calculatedMatrix.getTranslation(model.getPosition());
                    break;
                case Operation.ROTATE:
                    var rot = new AxisAngle4f();
                    calculatedMatrix.getRotation(rot);
                    model.setRotation(new Vector3f(rot.x, rot.y, rot.z));
                    model.setRotationAngle(rot.angle);
                    break;
                case Operation.SCALE:
                    calculatedMatrix.getScale(model.getScale());
                    break;
                default:
            }
        }
    }

    private void openContextMenu() {
        var size = 20;
        ImGui.textColored(0.3f, 0.3f, 0.9f, 1.0f, "Add");
        ImGui.separator();
        ImGui.image(TextureManager.getInstance().getTexture("src/main/resources/images/mesh_icons/cone.png").getId(), size, size);
        ImGui.sameLine();
        if (ImGui.menuItem("Cone")) {
            String key = "Model " + scene.getModels().size();
            scene.getModels().put(key, ModelManager.getInstance().clone("Cone"));
            scene.setSelectedModel(key);
        }
        ImGui.image(TextureManager.getInstance().getTexture("src/main/resources/images/mesh_icons/cube.png").getId(), size, size);
        ImGui.sameLine();
        if (ImGui.menuItem("Cube")) {
            String key = "Model " + scene.getModels().size();
            scene.getModels().put(key, ModelManager.getInstance().clone("Cube"));
            scene.setSelectedModel(key);
        }
        ImGui.image(TextureManager.getInstance().getTexture("src/main/resources/images/mesh_icons/cylinder.png").getId(), size, size);
        ImGui.sameLine();
        if (ImGui.menuItem("Cylinder")) {
            String key = "Model " + scene.getModels().size();
            scene.getModels().put(key, ModelManager.getInstance().clone("Cylinder"));
            scene.setSelectedModel(key);
        }
        ImGui.image(TextureManager.getInstance().getTexture("src/main/resources/images/mesh_icons/grid.png").getId(), size, size);
        ImGui.sameLine();
        if (ImGui.menuItem("Grid")) {
            String key = "Model " + scene.getModels().size();
            scene.getModels().put(key, ModelManager.getInstance().clone("Grid"));
            scene.setSelectedModel(key);
        }
        ImGui.image(TextureManager.getInstance().getTexture("src/main/resources/images/mesh_icons/sphere.png").getId(), size, size);
        ImGui.sameLine();
        if (ImGui.menuItem("Sphere")) {
            String key = "Model " + scene.getModels().size();
            scene.getModels().put(key, ModelManager.getInstance().clone("Sphere"));
            scene.setSelectedModel(key);
        }
        ImGui.separator();
        if (ImGui.menuItem("Torus")) {
            String key = "Model " + scene.getModels().size();
            scene.getModels().put(key, ModelManager.getInstance().clone("Torus"));
            scene.setSelectedModel(key);
        }
        if (ImGui.menuItem("Icosphere")) {
            String key = "Model " + scene.getModels().size();
            scene.getModels().put(key, ModelManager.getInstance().clone("Icosphere"));
            scene.setSelectedModel(key);
        }
        if (ImGui.menuItem("Monkey")) {
            String key = "Model " + scene.getModels().size();
            scene.getModels().put(key, ModelManager.getInstance().clone("Monkey"));
            scene.setSelectedModel(key);
        }
        ImGui.endPopup();

    }

}
