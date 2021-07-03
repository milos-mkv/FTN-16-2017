package gui.components;

import core.Scene;
import core.Settings;
import gui.Dock;
import imgui.ImGui;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import managers.Logger;
import managers.TextureManager;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;

import java.util.Objects;

import static utils.Utils.matrix4x4ToFloatBuffer;

public class ViewportDock implements Dock {

    @Override
    public void render() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.begin("Viewport", ImGuiWindowFlags.NoScrollbar);

        ImGui.image(Scene.getFrameBuffer().getTexture(), ImGui.getWindowSizeX(), ImGui.getWindowSizeY(), 0, 1, 1, 0);
        Scene.getFPSCamera().setAspect(ImGui.getWindowSize().x / ImGui.getWindowSize().y);

        manipulate();

        ImGui.setCursorPos(10, 40);
        if(ImGui.imageButton(Objects.requireNonNull(TextureManager.getTexture("src/main/resources/images/move.png")).getId(),
                20, 20)) {
            Settings.CurrentGizmoMode = Operation.TRANSLATE;
            Logger.log(Logger.Level.INFO, "Selected Translate Mode");
        }
        ImGui.setCursorPos(10, 70);
        if(ImGui.imageButton(Objects.requireNonNull(TextureManager.getTexture("src/main/resources/images/scale.png")).getId(),
                20, 20)) {
            Settings.CurrentGizmoMode = Operation.SCALE;
            Logger.log(Logger.Level.INFO, "Selected Scale Mode");
        }
        ImGui.setCursorPos(10, 100);
        if(ImGui.imageButton(Objects.requireNonNull(TextureManager.getTexture("src/main/resources/images/rotation.png")).getId(),
                20, 20)) {
            Settings.CurrentGizmoMode = Operation.ROTATE;
            Logger.log(Logger.Level.INFO, "Selected Rotate Mode");
        }

        ImGui.end();
        ImGui.popStyleVar();
    }


    private void manipulate() {
        if (Scene.SelectedModel != null) {
            ImGuizmo.setOrthographic(false);
            ImGuizmo.setEnabled(true);
            ImGuizmo.setDrawList();

            ImGuizmo.setRect(ImGui.getWindowPos().x, ImGui.getWindowPos().y, ImGui.getWindowWidth(), ImGui.getWindowHeight());

            var view = matrix4x4ToFloatBuffer(Scene.getFPSCamera().getViewMatrix());
            var proj = matrix4x4ToFloatBuffer(Scene.getFPSCamera().getProjectionMatrix());
            var transform = matrix4x4ToFloatBuffer(Scene.getModels().get(Scene.SelectedModel).getTransform());

            ImGuizmo.manipulate(view, proj, transform, Settings.CurrentGizmoMode, Mode.WORLD);

            if (ImGuizmo.isUsing()) {
                Matrix4f calculatedMatrix = new Matrix4f().set(transform);
                var rot = new AxisAngle4f();
                calculatedMatrix.getRotation(rot);
                calculatedMatrix.getTranslation(Scene.getModels().get(Scene.SelectedModel).getPosition());
                calculatedMatrix.getScale(Scene.getModels().get(Scene.SelectedModel).getScale());
            }
        }

    }

}
