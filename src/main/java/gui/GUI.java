package gui;

import gui.components.*;
import utils.Renderable;

import java.util.ArrayList;
import java.util.List;

public abstract class GUI {

    private static final List<Renderable> renderables = new ArrayList<>();

    public static void initialize() {
        renderables.add(new MainMenuBar());
        renderables.add(new ShadowPropertiesDock());
        renderables.add(new ShaderEditorDock());
        renderables.add(new ViewportDock());
        renderables.add(new ConsoleDock());
        renderables.add(new ScenePropertiesDock());
        renderables.add(new SplashModal());
        renderables.add(new ModelPropertiesDock());
    }

    public static void render() {
        renderables.forEach(Renderable::render);
    }

}
