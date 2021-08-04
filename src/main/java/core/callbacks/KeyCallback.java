package core.callbacks;

import core.Settings;
import imgui.ImGui;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiConfigFlags;
import managers.Console;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

public class KeyCallback extends GLFWKeyCallback {
    @Override
    public synchronized void invoke(long window, int key, int scancode, int action, int mods) {
        if (key == GLFW.GLFW_KEY_S && action == GLFW.GLFW_RELEASE && !ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.NoMouse)) {
            Settings.CurrentGizmoMode = Operation.SCALE;
            Console.log(Console.Level.INFO, "Selectede scale mode!");
        }
        if (key == GLFW.GLFW_KEY_T && action == GLFW.GLFW_RELEASE && !ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.NoMouse)) {
            Settings.CurrentGizmoMode = Operation.TRANSLATE;
            Console.log(Console.Level.INFO, "Selectede translate mode!");
        }
        if (key == GLFW.GLFW_KEY_R && action == GLFW.GLFW_RELEASE && !ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.NoMouse)) {
            Settings.CurrentGizmoMode = Operation.ROTATE;
            Console.log(Console.Level.INFO, "Selectede rotate mode!");
        }
    }
}
