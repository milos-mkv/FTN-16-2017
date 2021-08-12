package gui.components;

import core.Assets;
import core.Settings;
import gui.Dock;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import managers.Console;

public class ConsoleDock implements Dock {

    private int counter = 0;

    @Override
    public void render() {
        if (!Settings.ShowConsoleDock.get()) {
            return;
        }

        ImGui.begin("Console", Settings.ShowConsoleDock, ImGuiWindowFlags.HorizontalScrollbar);

        ImGui.pushFont(Assets.getInstance().getFont("CONSOLE_FONT"));
        Console.getLogs().forEach(ImGui::text);

        if(counter != Console.getLogs().size()) {
            counter = Console.getLogs().size();
            ImGui.setScrollHereY();
        }

        ImGui.popFont();

        ImGui.end();
    }
}
