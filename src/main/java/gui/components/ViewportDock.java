package gui.components;

import core.Scene;
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

public class ViewportDock implements Dock {

    @Override
    public void render() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.begin("Viewport", ImGuiWindowFlags.NoScrollbar);

        ImGui.image(Scene.getFrameBuffer().getTexture(), ImGui.getWindowSizeX(), ImGui.getWindowSizeY(), 0, 1, 1, 0);
        Scene.getFPSCamera().setAspect(ImGui.getWindowSize().x / ImGui.getWindowSize().y);

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

            ImGuizmo.manipulate(view, proj, arr, Operation.TRANSLATE, Mode.WORLD);

            if (ImGuizmo.isUsing()) {
                Matrix4f calculatedMatrix = new Matrix4f().set(arr);
                var rot = new AxisAngle4f();
                calculatedMatrix.getRotation(rot);
                calculatedMatrix.getTranslation(Scene.getModels().get(Scene.SelectedModel).getPosition());
                calculatedMatrix.getScale(Scene.getModels().get(Scene.SelectedModel).getScale());
            }
        }

        ImGui.setCursorPos(10, 40);
        if(ImGui.imageButton(TextureManager.getTexture("src/main/resources/images/move.png").getId(), 20, 20)) {
            Logger.log(Logger.Level.INFO, "Selected Translate Mode");
        }
        ImGui.setCursorPos(10, 70);
        if(ImGui.imageButton(TextureManager.getTexture("src/main/resources/images/scale.png").getId(), 20, 20)) {
            Logger.log(Logger.Level.INFO, "Selected Scale Mode");
        }
        ImGui.setCursorPos(10, 100);
        if(ImGui.imageButton(TextureManager.getTexture("src/main/resources/images/rotation.png").getId(), 20, 20)) {
            Logger.log(Logger.Level.INFO, "Selected Rotate Mode");
        }

        ImGui.end();
        ImGui.popStyleVar();
    }

}
