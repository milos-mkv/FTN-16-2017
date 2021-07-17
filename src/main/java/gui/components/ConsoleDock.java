package gui.components;

import core.Assets;
import core.Settings;
import core.Window;
import gui.Dock;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import managers.Logger;

public class ConsoleDock implements Dock {

    @Override
    public void render() {
        if (!Settings.ShowConsoleDock.get()) {
            return;
        }

        ImGui.begin("Console", Settings.ShowConsoleDock, ImGuiWindowFlags.HorizontalScrollbar);
        ImGui.pushFont(Assets.Fonts.get("CODE_FONT"));
        Logger.getLogs().forEach(ImGui::text);
        ImGui.popFont();
        ImGui.end();
    }
}
