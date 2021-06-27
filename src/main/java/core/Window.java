package core;

import imgui.ImFont;
import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import lombok.Getter;
import org.joml.Vector4f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.util.Objects;

public abstract class Window {

    protected final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    protected final ImGuiImplGl3  imGuiGl3  = new ImGuiImplGl3();

    public static ImFont codeFont;

    @Getter
    protected static long handle;

    @Getter
    protected static Vector4f mouse = new Vector4f();

    protected void initialize() {
        initializeWindow();
        initializeImGui();
        initializeImGuiStyle();
        imGuiGlfw.init(handle, true);
        imGuiGl3.init("#version 130");
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

    private void initializeWindow() {
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

        handle = GLFW.glfwCreateWindow(Constants.WINDOW_DEFAULT_WIDTH, Constants.WINDOW_DEFAULT_HEIGHT,
                "Simple OpenGL Renderer", MemoryUtil.NULL, MemoryUtil.NULL);
        if (handle == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        final GLFWVidMode vidmode = Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()));
        GLFW.glfwSetWindowPos(handle, (vidmode.width() - Constants.WINDOW_DEFAULT_WIDTH) / 2,
                (vidmode.height() - Constants.WINDOW_DEFAULT_HEIGHT) / 2);

        GLFW.glfwMakeContextCurrent(handle);
        GLFW.glfwSwapInterval(GLFW.GLFW_TRUE);
        GLFW.glfwShowWindow(handle);

        GLFW.glfwSetCursorPosCallback(handle, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xPos, double yPos) {
                mouse.x = (float) xPos;
                mouse.y = (float) yPos;
            }
        });

        GLFW.glfwSetMouseButtonCallback(handle, new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && action == GLFW.GLFW_PRESS) {
                    GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
                }
                if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && action == GLFW.GLFW_RELEASE) {
                    GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
                }
            }
        });


        GL.createCapabilities();
    }

    private void initializeImGui() {
        ImGui.createContext();
        ImGui.styleColorsDark();
        ImGui.getIO().addConfigFlags(ImGuiConfigFlags.DockingEnable | ImGuiConfigFlags.ViewportsEnable);
        ImGui.getIO().getFonts().addFontFromFileTTF(Constants.IMGUI_DEFAULT_FONT_PATH, Constants.IMGUI_DEFAULT_FONT_SIZE);
        codeFont = ImGui.getIO().getFonts().addFontFromFileTTF("src/main/resources/fonts/CONSOLA.ttf", 16.f);
    }

    private void initializeImGuiStyle() {
        ImGuiStyle style = ImGui.getStyle();
        float[][] colors = style.getColors();

        colors[ImGuiCol.Text]                   = new float[]{1.000f, 1.000f, 1.000f, 1.000f};
        colors[ImGuiCol.TextDisabled]           = new float[]{0.500f, 0.500f, 0.500f, 1.000f};
        colors[ImGuiCol.WindowBg]               = new float[]{0.180f, 0.180f, 0.180f, 1.000f};
        colors[ImGuiCol.ChildBg]                = new float[]{0.280f, 0.280f, 0.280f, 0.000f};
        colors[ImGuiCol.PopupBg]                = new float[]{0.313f, 0.313f, 0.313f, 1.000f};
        colors[ImGuiCol.Border]                 = new float[]{0.266f, 0.266f, 0.266f, 1.000f};
        colors[ImGuiCol.BorderShadow]           = new float[]{0.000f, 0.000f, 0.000f, 0.000f};
        colors[ImGuiCol.FrameBg]                = new float[]{0.160f, 0.160f, 0.160f, 1.000f};
        colors[ImGuiCol.FrameBgHovered]         = new float[]{0.200f, 0.200f, 0.200f, 1.000f};
        colors[ImGuiCol.FrameBgActive]          = new float[]{0.280f, 0.280f, 0.280f, 1.000f};
        colors[ImGuiCol.TitleBg]                = new float[]{0.148f, 0.148f, 0.148f, 1.000f};
        colors[ImGuiCol.TitleBgActive]          = new float[]{0.148f, 0.148f, 0.148f, 1.000f};
        colors[ImGuiCol.TitleBgCollapsed]       = new float[]{0.148f, 0.148f, 0.148f, 1.000f};
        colors[ImGuiCol.MenuBarBg]              = new float[]{0.195f, 0.195f, 0.195f, 1.000f};
        colors[ImGuiCol.ScrollbarBg]            = new float[]{0.160f, 0.160f, 0.160f, 1.000f};
        colors[ImGuiCol.ScrollbarGrab]          = new float[]{0.277f, 0.277f, 0.277f, 1.000f};
        colors[ImGuiCol.ScrollbarGrabHovered]   = new float[]{0.300f, 0.300f, 0.300f, 1.000f};
        colors[ImGuiCol.ScrollbarGrabActive]    = new float[]{1.000f, 0.391f, 0.000f, 1.000f};
        colors[ImGuiCol.CheckMark]              = new float[]{1.000f, 1.000f, 1.000f, 1.000f};
        colors[ImGuiCol.SliderGrab]             = new float[]{0.391f, 0.391f, 0.391f, 1.000f};
        colors[ImGuiCol.SliderGrabActive]       = new float[]{1.000f, 0.391f, 0.000f, 1.000f};
        colors[ImGuiCol.Button]                 = new float[]{1.000f, 1.000f, 1.000f, 0.000f};
        colors[ImGuiCol.ButtonHovered]          = new float[]{1.000f, 1.000f, 1.000f, 0.156f};
        colors[ImGuiCol.ButtonActive]           = new float[]{1.000f, 1.000f, 1.000f, 0.391f};
        colors[ImGuiCol.Header]                 = new float[]{0.313f, 0.313f, 0.313f, 1.000f};
        colors[ImGuiCol.HeaderHovered]          = new float[]{0.469f, 0.469f, 0.469f, 1.000f};
        colors[ImGuiCol.HeaderActive]           = new float[]{0.469f, 0.469f, 0.469f, 1.000f};
        colors[ImGuiCol.Separator]              = colors[ImGuiCol.Border];
        colors[ImGuiCol.SeparatorHovered]       = new float[]{0.391f, 0.391f, 0.391f, 1.000f};
        colors[ImGuiCol.SeparatorActive]        = new float[]{1.000f, 0.391f, 0.000f, 1.000f};
        colors[ImGuiCol.ResizeGrip]             = new float[]{0.000f, 0.000f, 0.000f, 0.000f};
        colors[ImGuiCol.ResizeGripHovered]      = new float[]{0.000f, 0.000f, 0.000f, 0.000f};
        colors[ImGuiCol.ResizeGripActive]       = new float[]{0.000f, 0.000f, 0.000f, 0.000f};
        colors[ImGuiCol.Tab]                    = new float[]{0.098f, 0.098f, 0.098f, 1.000f};
        colors[ImGuiCol.TabHovered]             = new float[]{0.352f, 0.352f, 0.352f, 1.000f};
        colors[ImGuiCol.TabActive]              = new float[]{0.195f, 0.195f, 0.195f, 1.000f};
        colors[ImGuiCol.TabUnfocused]           = new float[]{0.098f, 0.098f, 0.098f, 1.000f};
        colors[ImGuiCol.TabUnfocusedActive]     = new float[]{0.195f, 0.195f, 0.195f, 1.000f};
        colors[ImGuiCol.DockingPreview]         = new float[]{1.000f, 0.391f, 0.000f, 0.781f};
        colors[ImGuiCol.DockingEmptyBg]         = new float[]{0.180f, 0.180f, 0.180f, 1.000f};
        colors[ImGuiCol.PlotLines]              = new float[]{0.469f, 0.469f, 0.469f, 1.000f};
        colors[ImGuiCol.PlotLinesHovered]       = new float[]{1.000f, 0.391f, 0.000f, 1.000f};
        colors[ImGuiCol.PlotHistogram]          = new float[]{0.586f, 0.586f, 0.586f, 1.000f};
        colors[ImGuiCol.PlotHistogramHovered]   = new float[]{1.000f, 0.391f, 0.000f, 1.000f};
        colors[ImGuiCol.TextSelectedBg]         = new float[]{1.000f, 1.000f, 1.000f, 0.156f};
        colors[ImGuiCol.DragDropTarget]         = new float[]{1.000f, 0.391f, 0.000f, 1.000f};
        colors[ImGuiCol.NavHighlight]           = new float[]{1.000f, 0.391f, 0.000f, 1.000f};
        colors[ImGuiCol.NavWindowingHighlight]  = new float[]{1.000f, 0.391f, 0.000f, 1.000f};
        colors[ImGuiCol.NavWindowingDimBg]      = new float[]{0.000f, 0.000f, 0.000f, 0.586f};
        colors[ImGuiCol.ModalWindowDimBg]       = new float[]{0.000f, 0.000f, 0.000f, 0.586f};

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

    }

}
