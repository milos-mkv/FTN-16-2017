package gui;

import imgui.ImGui;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.util.nfd.NativeFileDialog;

public interface GUIControls {

    static void controlDragFloat3(String text, Vector3f vec, float min, float max) {
        var buffer = new float[]{vec.x, vec.y, vec.z};
        ImGui.text(text);
        ImGui.setNextItemWidth(ImGui.getColumnWidth());
        ImGui.dragFloat3("##" + text, buffer, 0.01f, min, max);
        vec.set(buffer);
    }

    static void controlRGB(String label, Vector3f vec) {
        var buffer = new float[]{vec.x, vec.y, vec.z};
        ImGui.text(label);
        ImGui.setNextItemWidth(ImGui.getColumnWidth());
        ImGui.colorEdit3("##" + label, buffer);
        vec.set(buffer);
    }

    static float controlDragFloat(String label, float value) {
        var buffer = new float[]{value};
        ImGui.text(label);
        ImGui.setNextItemWidth(ImGui.getColumnWidth());
        ImGui.dragFloat("##" + label, buffer);
        return buffer[0];
    }

    static String controlOpenFileDialog() {
        try {
            var pointerBuffer = PointerBuffer.allocateDirect(1);
            NativeFileDialog.NFD_OpenDialog((CharSequence) null, null, pointerBuffer);
            return pointerBuffer.getStringASCII().replace("\\", "/");
        } catch (Exception e) {
            return null;
        }
    }

}
