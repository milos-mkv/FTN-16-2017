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
        Console.getLogs().forEach(log -> {
            ImGui.text(log.time);
            ImGui.sameLine();
            switch (log.level) {
                case "ERROR  ": ImGui.textColored(1.0F, 0.1F, 0.1F, 1.0F, log.level); break;
                case "INFO   ": ImGui.textColored(0.1F, 1.0F, 0.1F, 1.0F, log.level); break;
                case "WARNING": ImGui.textColored(1.0F, 0.6F, 0.1F, 1.0F, log.level); break;
                default:        ImGui.text(log.level); break;
            }
            ImGui.sameLine();
            ImGui.text(log.message);
        });

        if(counter != Console.getLogs().size()) {
            counter = Console.getLogs().size();
            ImGui.setScrollHereY();
        }

        ImGui.popFont();

        ImGui.end();
    }
}
