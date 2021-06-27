package gui;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import managers.ErrorManager;
import managers.TextureManager;
import org.joml.Vector3f;

public interface GUIComponents {

     static void renderDragFloat3(String text, Vector3f vec) {
        var buffer = new float[] { vec.x, vec.y, vec.z };
        ImGui.text(text);
        ImGui.dragFloat3("##" + text, buffer, 0.01f);
        vec.set(buffer);
    }

     static void renderDragFloat3(String text, Vector3f vec, float min, float max) {
        var buffer = new float[] { vec.x, vec.y, vec.z };
        ImGui.text(text);
        ImGui.dragFloat3("##" + text, buffer, 0.01f, min, max);
        vec.set(buffer);
    }

     static void errorDialog(String title) {
        var open = new ImBoolean(true);
        if (ImGui.beginPopupModal(title, open, ImGuiWindowFlags.NoScrollbar)) {
            ImGui.image(TextureManager.getTexture("src/main/resources/images/error_icon.png").getId(), 40,40);
            ImGui.sameLine();
            ImGui.textWrapped(ErrorManager.getLatestError());
            ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.7f, 0.1f, 0.15f, 1.0f);
            ImGui.setCursorPos(ImGui.getWindowWidth() - 55, ImGui.getWindowHeight() - 35);
            if (ImGui.button("Close")) {
                ImGui.closeCurrentPopup();
            }
            ImGui.popStyleColor();
            ImGui.popStyleColor();
            ImGui.endPopup();
        }
    }

}
