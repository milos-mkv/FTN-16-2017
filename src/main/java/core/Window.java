package core;

import core.callbacks.CursorPosCallback;
import core.callbacks.MouseButtonCallback;
import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import lombok.Getter;
import org.joml.Vector4f;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.util.Objects;

public abstract class Window {

    protected final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    protected final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    @Getter
    protected static long handle;

    @Getter
    protected static Vector4f mouse = new Vector4f();

    protected void initialize() {
        initializeWindow();
        initializeImGui();
        initializeImGuiStyle();
    }

    protected void dispose() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
        Callbacks.glfwFreeCallbacks(handle);
        GLFW.glfwDestroyWindow(handle);
        GLFW.glfwTerminate();
        Objects.requireNonNull(GLFW.glfwSetErrorCallback(null)).free();
    }

    private synchronized void initializeWindow() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new RuntimeException("Failed to initialize GLFW!");
        }

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);

        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        }

        handle = GLFW.glfwCreateWindow(Constants.WINDOW_DEFAULT_WIDTH, Constants.WINDOW_DEFAULT_HEIGHT, "Hatsune Miku",
            MemoryUtil.NULL, MemoryUtil.NULL);

        if (handle == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        final GLFWVidMode vidmode = Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()));
        GLFW.glfwSetWindowPos(handle,
            (vidmode.width() - Constants.WINDOW_DEFAULT_WIDTH) / 2, (vidmode.height() - Constants.WINDOW_DEFAULT_HEIGHT) / 2);

        GLFW.glfwMakeContextCurrent(handle);
        GLFW.glfwSwapInterval(GLFW.GLFW_TRUE);
        GLFW.glfwShowWindow(handle);

        GLFW.glfwSetCursorPosCallback(handle, new CursorPosCallback());
        GLFW.glfwSetMouseButtonCallback(handle, new MouseButtonCallback());

        GL.createCapabilities();
    }

    private void initializeImGui() {
        ImGui.createContext();
        ImGui.styleColorsDark();
        ImGui.getIO().addConfigFlags(ImGuiConfigFlags.DockingEnable | ImGuiConfigFlags.ViewportsEnable);

        Assets.Fonts.put(
            "DEFAULT_FONT",
            ImGui.getIO().getFonts().addFontFromFileTTF(Constants.DEFAULT_FONT_PATH, Constants.DEFAULT_FONT_SIZE));
        Assets.Fonts.put(
            "CONSOLE_FONT",
            ImGui.getIO().getFonts().addFontFromFileTTF(Constants.CONSOLE_FONT_PATH, Constants.CONSOLE_FONT_SIZE));
        Assets.Fonts.put(
            "SPLASH_FONT",
            ImGui.getIO().getFonts().addFontFromFileTTF(Constants.JAPANESE_FONT_PATH, Constants.JAPANESE_FONT_SIZE));

        imGuiGlfw.init(handle, true);
        imGuiGl3.init("#version 130");
    }

    private void initializeImGuiStyle() {
        ImGuiStyle style = ImGui.getStyle();
        float[][] colors = style.getColors();

        colors[ImGuiCol.Text] = new float[]{0.95f, 0.96f, 0.98f, 1.00f};
        colors[ImGuiCol.TextDisabled] = new float[]{0.36f, 0.42f, 0.47f, 1.00f};
        colors[ImGuiCol.WindowBg] = new float[]{0.11f, 0.15f, 0.17f, 1.00f};
        colors[ImGuiCol.ChildBg] = new float[]{0.15f, 0.18f, 0.22f, 1.00f};
        colors[ImGuiCol.PopupBg] = new float[]{0.08f, 0.08f, 0.08f, 0.94f};
        colors[ImGuiCol.Border] = new float[]{0.08f, 0.10f, 0.12f, 1.00f};
        colors[ImGuiCol.BorderShadow] = new float[]{0.00f, 0.00f, 0.00f, 0.00f};
        colors[ImGuiCol.FrameBg] = new float[]{0.20f, 0.25f, 0.29f, 1.00f};
        colors[ImGuiCol.FrameBgHovered] = new float[]{0.12f, 0.20f, 0.28f, 1.00f};
        colors[ImGuiCol.FrameBgActive] = new float[]{0.09f, 0.12f, 0.14f, 1.00f};
        colors[ImGuiCol.TitleBg] = new float[]{0.09f, 0.12f, 0.14f, 0.65f};
        colors[ImGuiCol.TitleBgActive] = new float[]{0.08f, 0.10f, 0.12f, 1.00f};
        colors[ImGuiCol.TitleBgCollapsed] = new float[]{0.00f, 0.00f, 0.00f, 0.51f};
        colors[ImGuiCol.MenuBarBg] = new float[]{0.15f, 0.18f, 0.22f, 1.00f};
        colors[ImGuiCol.ScrollbarBg] = new float[]{0.02f, 0.02f, 0.02f, 0.39f};
        colors[ImGuiCol.ScrollbarGrab] = new float[]{0.20f, 0.25f, 0.29f, 1.00f};
        colors[ImGuiCol.ScrollbarGrabHovered] = new float[]{0.18f, 0.22f, 0.25f, 1.00f};
        colors[ImGuiCol.ScrollbarGrabActive] = new float[]{0.09f, 0.21f, 0.31f, 1.00f};
        colors[ImGuiCol.CheckMark] = new float[]{0.28f, 0.56f, 1.00f, 1.00f};
        colors[ImGuiCol.SliderGrab] = new float[]{0.28f, 0.56f, 1.00f, 1.00f};
        colors[ImGuiCol.SliderGrabActive] = new float[]{0.37f, 0.61f, 1.00f, 1.00f};
        colors[ImGuiCol.Button] = new float[]{0.20f, 0.25f, 0.29f, 1.00f};
        colors[ImGuiCol.ButtonHovered] = new float[]{0.28f, 0.56f, 1.00f, 1.00f};
        colors[ImGuiCol.ButtonActive] = new float[]{0.06f, 0.53f, 0.98f, 1.00f};
        colors[ImGuiCol.Header] = new float[]{0.20f, 0.25f, 0.29f, 0.55f};
        colors[ImGuiCol.HeaderHovered] = new float[]{0.26f, 0.59f, 0.98f, 0.80f};
        colors[ImGuiCol.HeaderActive] = new float[]{0.26f, 0.59f, 0.98f, 1.00f};
        colors[ImGuiCol.Separator] = new float[]{0.20f, 0.25f, 0.29f, 1.00f};
        colors[ImGuiCol.SeparatorHovered] = new float[]{0.10f, 0.40f, 0.75f, 0.78f};
        colors[ImGuiCol.SeparatorActive] = new float[]{0.10f, 0.40f, 0.75f, 1.00f};
        colors[ImGuiCol.ResizeGrip] = new float[]{0.26f, 0.59f, 0.98f, 0.25f};
        colors[ImGuiCol.ResizeGripHovered] = new float[]{0.26f, 0.59f, 0.98f, 0.67f};
        colors[ImGuiCol.ResizeGripActive] = new float[]{0.26f, 0.59f, 0.98f, 0.95f};
        colors[ImGuiCol.Tab] = new float[]{0.11f, 0.15f, 0.17f, 1.00f};
        colors[ImGuiCol.TabHovered] = new float[]{0.26f, 0.59f, 0.98f, 0.80f};
        colors[ImGuiCol.TabActive] = new float[]{0.20f, 0.25f, 0.29f, 1.00f};
        colors[ImGuiCol.TabUnfocused] = new float[]{0.11f, 0.15f, 0.17f, 1.00f};
        colors[ImGuiCol.TabUnfocusedActive] = new float[]{0.11f, 0.15f, 0.17f, 1.00f};
        colors[ImGuiCol.PlotLines] = new float[]{0.61f, 0.61f, 0.61f, 1.00f};
        colors[ImGuiCol.PlotLinesHovered] = new float[]{1.00f, 0.43f, 0.35f, 1.00f};
        colors[ImGuiCol.PlotHistogram] = new float[]{0.90f, 0.70f, 0.00f, 1.00f};
        colors[ImGuiCol.PlotHistogramHovered] = new float[]{1.00f, 0.60f, 0.00f, 1.00f};
        colors[ImGuiCol.TextSelectedBg] = new float[]{0.26f, 0.59f, 0.98f, 0.35f};
        colors[ImGuiCol.DragDropTarget] = new float[]{1.00f, 1.00f, 0.00f, 0.90f};
        colors[ImGuiCol.NavHighlight] = new float[]{0.26f, 0.59f, 0.98f, 1.00f};
        colors[ImGuiCol.NavWindowingHighlight] = new float[]{1.00f, 1.00f, 1.00f, 0.70f};
        colors[ImGuiCol.NavWindowingDimBg] = new float[]{0.80f, 0.80f, 0.80f, 0.20f};
        colors[ImGuiCol.ModalWindowDimBg] = new float[]{0.80f, 0.80f, 0.80f, 0.35f};

        style.setColors(colors);
        style.setChildRounding(4.0f);
        style.setFrameBorderSize(1.0f);
        style.setFrameRounding(2.0f);
        style.setGrabMinSize(7.0f);
        style.setPopupRounding(0.0f);
        style.setScrollbarRounding(12.0f);
        style.setScrollbarSize(13.0f);
        style.setTabBorderSize(1.0f);
        style.setTabRounding(0.0f);
        style.setWindowRounding(1.0f);
        style.setChildBorderSize(1.0f);
    }

}
