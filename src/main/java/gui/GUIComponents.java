package gui;

import imgui.ImGui;
import org.joml.Vector3f;

public abstract class GUIComponents {

    public static void renderDragFloat3(String text, Vector3f vec) {
        float[] buffer = { vec.x, vec.y, vec.z };
        ImGui.text(text);
        ImGui.dragFloat3("##" + text, buffer, 0.01f);
        vec.set(buffer);
    }

    public static void renderDragFloat3(String text, Vector3f vec, float min, float max) {
        float[] buffer = { vec.x, vec.y, vec.z };
        ImGui.text(text);
        ImGui.dragFloat3("##" + text, buffer, 0.01f, min, max);
        vec.set(buffer);
    }

}
