package core;

import imgui.type.ImBoolean;

public abstract class Settings {

    public static ImBoolean ShowLightPropertiesDock = new ImBoolean(true);
    public static ImBoolean ShowSceneItemsDock = new ImBoolean(true);

    public static boolean EnableGrid = true;
}
