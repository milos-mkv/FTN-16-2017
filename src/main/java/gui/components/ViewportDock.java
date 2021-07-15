package gui.components;

import core.Constants;
import core.Scene;
import core.Settings;
import gui.Dock;
import imgui.ImGui;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import lombok.extern.java.Log;
import managers.Logger;
import managers.TextureManager;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Objects;

import static utils.Utils.matrix4x4ToFloatBuffer;

@Log
public class ViewportDock implements Dock {

    @Override
    public synchronized void render() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.begin("Viewport", ImGuiWindowFlags.NoScrollbar);

        ImGui.image(Scene.getFrameBuffer().getTexture(), ImGui.getWindowSizeX(), ImGui.getWindowSizeY(), 0, 1, 1, 0);
        Scene.getFPSCamera().setAspect(ImGui.getWindowSize().x / ImGui.getWindowSize().y);

        manipulate();

        var size = 20;

        ImGui.setCursorPos(10, 40);
        if (ImGui.imageButton(Objects.requireNonNull(TextureManager.getTexture(Constants.ICON_TRANSLATE)).getId(),
                size, size)) {
            Settings.CurrentGizmoMode = Operation.TRANSLATE;
            Logger.log(Logger.Level.INFO, "Selected Translate Mode");
        }

        ImGui.setCursorPos(10, 70);
        if (ImGui.imageButton(Objects.requireNonNull(TextureManager.getTexture(Constants.ICON_SCALE)).getId(),
                size, size)) {
            Settings.CurrentGizmoMode = Operation.SCALE;
            Logger.log(Logger.Level.INFO, "Selected Scale Mode");
        }

        ImGui.setCursorPos(10, 100);
        if (ImGui.imageButton(Objects.requireNonNull(TextureManager.getTexture(Constants.ICON_ROTATE)).getId(),
                size, size)) {
            Settings.CurrentGizmoMode = Operation.ROTATE;
            Logger.log(Logger.Level.INFO, "Selected Rotate Mode");
        }

        ImGui.end();
        ImGui.popStyleVar();
    }


    private void manipulate() {
        if (Scene.SelectedModel == null) {
            return;
        }

        ImGuizmo.setOrthographic(false);
        ImGuizmo.setEnabled(true);
        ImGuizmo.setDrawList();
        ImGuizmo.setRect(ImGui.getWindowPosX(), ImGui.getWindowPosY(), ImGui.getWindowWidth(), ImGui.getWindowHeight());

        var model       = Scene.getModels().get(Scene.SelectedModel);
        var view        = matrix4x4ToFloatBuffer(Scene.getFPSCamera().getViewMatrix());
        var proj        = matrix4x4ToFloatBuffer(Scene.getFPSCamera().getProjectionMatrix());
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

}
