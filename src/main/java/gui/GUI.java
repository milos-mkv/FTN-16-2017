package gui;

import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import utils.Renderable;
import gui.components.*;

import java.util.ArrayList;
import java.util.List;

public abstract class GUI {

    private static final List<Renderable> renderables = new ArrayList<>();

    private GUI() { }

    public static void initialize() {
        renderables.add(new ModelPropertiesDock());
        renderables.add(new MainMenuBar());
        renderables.add(new ViewportDock());
        renderables.add(new ConsoleDock());
        renderables.add(new ScenePropertiesDock());
        renderables.add(new SplashModal()   );
    }

    public static void render() {
        renderables.forEach(Renderable::render);
    }

}
