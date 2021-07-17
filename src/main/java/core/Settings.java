package core;

import gfx.Texture;
import imgui.extension.imguizmo.flag.Operation;
import imgui.type.ImBoolean;

public abstract class Settings {

    public static ImBoolean ShowScenePropertiesDock = new ImBoolean(true);
    public static ImBoolean ShowModelPropertiesDock = new ImBoolean(true);
    public static ImBoolean ShowConsoleDock         = new ImBoolean(true);
    public static ImBoolean ShowSplashModal         = new ImBoolean(true);
    public static ImBoolean ShowTexturePreviewDock  = new ImBoolean(false);

    public static Texture TextureInPreview = null;

    public static int CurrentGizmoMode = Operation.TRANSLATE;

}
