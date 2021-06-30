package gui;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import managers.Logger;
import managers.TextureManager;
import org.joml.Vector3f;

import java.util.Arrays;

public interface GUIComponents {

    static void renderDragFloat3(String text, Vector3f vec) {
        var buffer = new float[]{vec.x, vec.y, vec.z};
        ImGui.text(text);
        ImGui.dragFloat3("##" + text, buffer, 0.01f);
        vec.set(buffer);
    }

    static void renderDragFloat3(String text, Vector3f vec, float min, float max) {
        var buffer = new float[]{vec.x, vec.y, vec.z};
        ImGui.text(text);
        ImGui.dragFloat3("##" + text, buffer, 0.01f, min, max);
        vec.set(buffer);
    }

    static void errorDialog(String title, String message) {
        var open = new ImBoolean(true);
        if (ImGui.beginPopupModal(title, open, ImGuiWindowFlags.NoScrollbar)) {
            ImGui.image(TextureManager.getTexture("src/main/resources/images/error_icon.png").getId(), 40, 40);
            ImGui.sameLine();
            ImGui.textWrapped(message);
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

    static void float3ControlXYZ(String label, Vector3f vec3, float min, float max) {
        float3ControlRGB(label, vec3, min, max, false);
    }
    static void float3ControlRGB(String label, Vector3f vec3, float min, float max) {
        float3ControlRGB(label, vec3, min, max, true);
    }

    static void float3Control(String label, Vector3f vec3, float min, float max) {
        float[] x = {vec3.x};
        float[] y = {vec3.y};
        float[] z = {vec3.z};

        ImGui.pushID(label);
        ImGui.text(label);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);

        ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.1f, 0.15f, 1.0f);

        ImGui.button(" X ");
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        ImGui.pushItemWidth(ImGui.calcItemWidth() / 3);
        ImGui.dragFloat("##X", x, 0.01F, min, max);

        ImGui.pushStyleColor(ImGuiCol.Button, 0.1f, 0.25f, 0.8f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.2f, 0.35f, 0.9f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.25f, 0.8f, 1.0f);

        ImGui.sameLine();
        ImGui.button(" Y ");
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        ImGui.dragFloat("##Y", y, 0.01F, min, max);
        ImGui.sameLine();

        ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1.0f);

        ImGui.button(" Z ");
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        ImGui.dragFloat("##Z", z, 0.01F, min, max);

        ImGui.popItemWidth();
        ImGui.popStyleVar();
        ImGui.popID();
        vec3.set(x[0], y[0], z[0]);

    }

    static void float3ControlRGB(String label, Vector3f vec3, float min, float max, boolean rgb) {
        var buffer = new float[] { vec3.x, vec3.y, vec3.z };
        ImGui.text(label);
        ImGui.colorEdit3("##"+label, buffer);
        vec3.set(buffer);
    }


}
