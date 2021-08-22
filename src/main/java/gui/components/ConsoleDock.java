package gui.components;

import core.Assets;
import core.Settings;
import gui.Dock;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import managers.Console;

public class ConsoleDock implements Dock {

    private int counter = 0;
    private final Assets assets;

    public ConsoleDock() {
        this.assets = Assets.getInstance();
    }

    @Override
    public void render() {
        if (!Settings.ShowConsoleDock.get()) {
            return;
        }

        ImGui.begin("Console", Settings.ShowConsoleDock, ImGuiWindowFlags.HorizontalScrollbar);
        ImGui.pushFont(assets.getFont("CONSOLE_FONT"));

        Console.getLogs().forEach(log -> {
            ImGui.text(log.time);
            ImGui.sameLine();
            switch (log.level) {
                case ERROR:   ImGui.textColored(1.0F, 0.1F, 0.1F, 1.0F, "ERROR  "); break;
                case INFO:    ImGui.textColored(0.1F, 1.0F, 0.1F, 1.0F, "INFO   "); break;
                case WARNING: ImGui.textColored(1.0F, 0.6F, 0.1F, 1.0F, "WARNING"); break;
                default:      ImGui.text("DEBUG  ");                                break;
            }
            ImGui.sameLine();
            ImGui.text("] " + log.message);
        });

        if (counter != Console.getLogs().size()) {
            counter  = Console.getLogs().size();
            ImGui.setScrollHereY();
        }

        ImGui.popFont();
        ImGui.end();
    }
}
