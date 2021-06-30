package core;

import imgui.type.ImBoolean;

public interface Settings {

    ImBoolean ShowScenePropertiesDock = new ImBoolean(true);
    ImBoolean ShowModelPropertiesDock = new ImBoolean(true);
    ImBoolean ShowConsoleDock         = new ImBoolean(true);


}
