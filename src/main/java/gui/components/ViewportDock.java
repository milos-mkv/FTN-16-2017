package gui.components;

import core.Constants;
import core.Scene;
import core.Settings;
import gfx.Texture;
import gui.Dock;
import imgui.ImGui;
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
import org.joml.Vector3f;
import org.w3c.dom.Text;

import java.util.Objects;

import static utils.Utils.matrix4x4ToFloatBuffer;

public class ViewportDock implements Dock {

    private final Scene scene;

    public ViewportDock() {
        this.scene = Scene.getInstance();
    }

    @Override
    public synchronized void render() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.begin("Viewport", ImGuiWindowFlags.NoScrollbar);

        ImGui.image(scene.getFrameBuffer().getTexture(), ImGui.getWindowSizeX(), ImGui.getWindowSizeY(), 0, 1, 1, 0);
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

        var model       = scene.getSelectedModel();
        var view        = matrix4x4ToFloatBuffer(scene.getCamera().getViewMatrix());
        var proj        = matrix4x4ToFloatBuffer(scene.getCamera().getProjectionMatrix());
        var transform   = matrix4x4ToFloatBuffer(model.getTransform());

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
        if (ImGui.menuItem("Cube")) { }
        ImGui.image(TextureManager.getInstance().getTexture("src/main/resources/images/mesh_icons/cylinder.png").getId(), size, size);
        ImGui.sameLine();
        if (ImGui.menuItem("Cylinder")) { }
        ImGui.image(TextureManager.getInstance().getTexture("src/main/resources/images/mesh_icons/grid.png").getId(), size, size);
        ImGui.sameLine();
        if (ImGui.menuItem("Grid")) { }
        ImGui.image(TextureManager.getInstance().getTexture("src/main/resources/images/mesh_icons/sphere.png").getId(), size, size);
        ImGui.sameLine();
        if (ImGui.menuItem("Sphere")) { }
        ImGui.separator();
        if (ImGui.menuItem("Torus")) { }
        if (ImGui.menuItem("Icosphere")) { }
        if (ImGui.menuItem("Monkey")) { }
        ImGui.endPopup();

    }

}
