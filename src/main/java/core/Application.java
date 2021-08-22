package core;

import imgui.ImGui;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.flag.ImGuiConfigFlags;
import org.lwjgl.glfw.GLFW;

public abstract class Application extends Window {

    public static void launch(final Application application) {
        application.initialize();
        application.onStart();
        application.run();
        application.onEnd();
        application.dispose();
    }

    protected void run() {
        while (!GLFW.glfwWindowShouldClose(handle)) {
            render(ImGui.getIO().getDeltaTime());
            startFrameImGui();
            renderImGui();
            endFrameImGui();
        }
    }

    protected abstract void render(float delta);

    protected abstract void renderImGui();

    protected abstract void onStart();

    protected abstract void onEnd();


    private void startFrameImGui() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();
        ImGui.dockSpaceOverViewport(ImGui.getMainViewport());
        ImGuizmo.beginFrame();
    }

    private void endFrameImGui() {
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }

        GLFW.glfwSwapBuffers(handle);
        mouse.z = mouse.x;
        mouse.w = mouse.y;
        GLFW.glfwPollEvents();
    }

}
