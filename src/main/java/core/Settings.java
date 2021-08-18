package core;

import imgui.extension.imguizmo.flag.Operation;
import imgui.type.ImBoolean;

public abstract class Settings {

    public static ImBoolean ShowScenePropertiesDock = new ImBoolean(true);
    public static ImBoolean ShowModelPropertiesDock = new ImBoolean(true);
    public static ImBoolean ShowSkyboxPropertiesDock = new ImBoolean(false);
    public static ImBoolean ShowShadowPropertiesDock = new ImBoolean(false);
    public static ImBoolean ShowConsoleDock = new ImBoolean(true);
    public static ImBoolean ShowSplashModal = new ImBoolean(false);
    public static ImBoolean ShowTexturePreviewDock = new ImBoolean(false);

    public static ImBoolean ToogleShadows = new ImBoolean(true);
    public static ImBoolean EnableDirectionalLight = new ImBoolean(true);

    public static ImBoolean ToggleSkyBox = new ImBoolean(false);
    public static ImBoolean ToggleGrid = new ImBoolean(true);

    public static int CurrentGizmoMode = Operation.TRANSLATE;
    public static int NextModelIndex = 0;
    public static String TextureForPreview = null;

    public static ImBoolean EnableLinePolygonMode = new ImBoolean(false);
    public static ImBoolean EnableFaceCulling = new ImBoolean(false);
    public static ImBoolean EnableMSAA = new ImBoolean(false);
    public static ImBoolean CapFPS = new ImBoolean(false);
    public static ImBoolean EnableShadows = new ImBoolean(false);

    public static float GLLineWidth = 1.0f;
}
