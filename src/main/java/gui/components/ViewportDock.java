package gui.components;

import core.Constants;
import core.Scene;
import core.Settings;
import core.Window;
import gfx.Model;
import gui.Dock;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import managers.Console;
import managers.ModelManager;
import managers.TextureManager;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

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
    private final TextureManager textureManager;
    private final ModelManager modelManager;
    public ViewportDock() {
        this.textureManager = TextureManager.getInstance();
        this.modelManager = ModelManager.getInstance();
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

        if (GLFW.glfwGetMouseButton(Window.getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS &&
                GLFW.glfwGetKey(Window.getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS) {
            glBindFramebuffer(GL_FRAMEBUFFER, scene.getSelectFrameBuffer().getId());
            int[] i = new int[1];
            glReadPixels((int) finalX, (int) finalY, 1, 1, GL_RED_INTEGER, GL_INT, i);
            for (Map.Entry<String, Model> mas : scene.getModels().entrySet()) {
                if (mas.getValue().getId() == i[0]) {
                    Scene.getInstance().setSelectedModel(mas.getKey());
                    break;
                }
            }
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }

        scene.getCamera().setAspect(ImGui.getWindowSize().x / ImGui.getWindowSize().y);

        manipulate();

        var size = 40;
        ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.2f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.1f, 0.1f, 0.1f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.3f, 0.3f, 0.3f, 1.0f);
        ImGui.setCursorPos(10, 40);
        if (ImGui.imageButton(textureManager.getTexture("src/main/resources/images/move.png").getId(), size, size)) {
            Settings.CurrentGizmoMode = Operation.TRANSLATE;
            Console.log(Console.Level.INFO, "Selected Translate Mode");
        }

        ImGui.setCursorPos(10, 90);
        if (ImGui.imageButton(textureManager.getTexture("src/main/resources/images/resize.png").getId(), size, size)) {
            Settings.CurrentGizmoMode = Operation.SCALE;
            Console.log(Console.Level.INFO, "Selected Scale Mode");
        }

        ImGui.setCursorPos(10, 140);
        if (ImGui.imageButton(textureManager.getTexture("src/main/resources/images/rotating.png").getId(), size, size)) {
            Settings.CurrentGizmoMode = Operation.ROTATE;
            Console.log(Console.Level.INFO, "Selected Rotate Mode");
        }
        ImGui.popStyleVar();

        if (ImGui.beginPopupContextWindow(ImGuiMouseButton.Middle)) {
            openContextMenu();
        }
        ImGui.popStyleColor(3);
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
        ImGui.textColored(1.0f, 0.5f, 0.25f, 1.0f, "Add");
        ImGui.separator();
        ImGui.image(textureManager.getTexture("src/main/resources/images/mesh_icons/cone.png").getId(), size, size);
        ImGui.sameLine();
        String key = "Model " + scene.getModels().size();
        int startSize = scene.getModels().size();
        if (ImGui.menuItem("Cone")) {
            scene.getModels().put(key, modelManager.clone("Cone"));
            scene.setSelectedModel(key);
        }
        ImGui.image(textureManager.getTexture("src/main/resources/images/mesh_icons/cube.png").getId(), size, size);
        ImGui.sameLine();
        if (ImGui.menuItem("Cube")) {
            scene.getModels().put(key, modelManager.clone("Cube"));
            scene.setSelectedModel(key);
        }
        ImGui.image(textureManager.getTexture("src/main/resources/images/mesh_icons/cylinder.png").getId(), size, size);
        ImGui.sameLine();
        if (ImGui.menuItem("Cylinder")) {
            scene.getModels().put(key, modelManager.clone("Cylinder"));
            scene.setSelectedModel(key);
        }
        ImGui.image(textureManager.getTexture("src/main/resources/images/mesh_icons/grid.png").getId(), size, size);
        ImGui.sameLine();
        if (ImGui.menuItem("Grid")) {
            scene.getModels().put(key, modelManager.clone("Grid"));
            scene.setSelectedModel(key);
        }
        ImGui.image(textureManager.getTexture("src/main/resources/images/mesh_icons/sphere.png").getId(), size, size);
        ImGui.sameLine();
        if (ImGui.menuItem("Sphere")) {
            scene.getModels().put(key, modelManager.clone("Sphere"));
            scene.setSelectedModel(key);
        }
        ImGui.separator();
        if (ImGui.menuItem("Torus")) {
            scene.getModels().put(key, modelManager.clone("Torus"));
            scene.setSelectedModel(key);
        }
        if (ImGui.menuItem("Icosphere")) {
            scene.getModels().put(key, modelManager.clone("Icosphere"));
            scene.setSelectedModel(key);
        }
        if (ImGui.menuItem("Monkey")) {
            scene.getModels().put(key, modelManager.clone("Monkey"));
            scene.setSelectedModel(key);
        }
        ImGui.separator();
        ImGui.image(textureManager.getTexture("src/main/resources/images/misicon1.png").getId(), 24, 24);

        ImGui.sameLine();

        if (ImGui.menuItem("Mikoto Misaka")) {
            scene.getModels().put(key, modelManager.clone("MikotoMisaka"));
            scene.setSelectedModel(key);
        }

        if(scene.getModels().size() > startSize) {
            Console.log(Console.Level.INFO, "Added new model to scene. @ " + System.identityHashCode(scene.getModels().get(key)));
        }

        ImGui.endPopup();

    }

}
