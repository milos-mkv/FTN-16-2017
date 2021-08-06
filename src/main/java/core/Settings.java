package core;

import imgui.extension.imguizmo.flag.Operation;
import imgui.type.ImBoolean;

public abstract class Settings {

    public static ImBoolean ShowScenePropertiesDock  = new ImBoolean(true);
    public static ImBoolean ShowModelPropertiesDock  = new ImBoolean(true);
    public static ImBoolean ShowShadowPropertiesDock = new ImBoolean(true);
    public static ImBoolean ShowConsoleDock          = new ImBoolean(true);
    public static ImBoolean ShowMeshInformationDock  = new ImBoolean(true);
    public static ImBoolean ShowSplashModal          = new ImBoolean(false);

    public static ImBoolean ToogleShadows = new ImBoolean(true);

    public static boolean ToggleSkyBox  = false;
    public static boolean ToggleGrid  = true;

    public static int CurrentGizmoMode = Operation.TRANSLATE;

}
