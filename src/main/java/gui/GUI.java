package gui;

import gui.components.*;
import utils.Renderable;

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
        renderables.add(new SplashModal());
        renderables.add(new TexturePreviewDock());

    }
    public static void render() {
        renderables.forEach(Renderable::render);
    }

}
