package gui;

import gui.components.*;
import utils.Renderable;

import java.util.ArrayList;
import java.util.List;

public class GUI implements Renderable {

    private final List<Renderable> renderables = new ArrayList<>();

    public GUI() {
        renderables.add(new MainMenuBar());
        renderables.add(new ShadowPropertiesDock());
        renderables.add(new ShaderEditorDock());
        renderables.add(new ViewportDock());
        renderables.add(new ConsoleDock());
        renderables.add(new ScenePropertiesDock());
        renderables.add(new SplashModal());
        renderables.add(new ModelPropertiesDock());
        renderables.add(new SkyboxPropertiesDock());
        renderables.add(new TexturePreviewDock());
//        renderables.add(new NodeEditorDock());
    }

    @Override
    public void render() {
        renderables.forEach(Renderable::render);
    }

}
