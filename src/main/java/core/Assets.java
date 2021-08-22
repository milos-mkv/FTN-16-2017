package core;

import imgui.ImFont;
import imgui.ImGui;
import utils.Disposable;

import java.util.HashMap;
import java.util.Map;

public class Assets  {

    private static Assets assets;

    public static Assets getInstance() {
        return assets == null ? assets = new Assets() : assets;
    }

    public final Map<String, ImFont> fonts = new HashMap<>();

    private Assets() {
        fonts.put("DEFAULT_FONT",
                ImGui.getIO().getFonts().addFontFromFileTTF("src/main/resources/fonts/font.ttf", 21.0F));
        fonts.put("CONSOLE_FONT",
                ImGui.getIO().getFonts().addFontFromFileTTF("src/main/resources/fonts/CONSOLA.ttf", 14.0F));
        fonts.put("CODE_FONT",
                ImGui.getIO().getFonts().addFontFromFileTTF("src/main/resources/fonts/CONSOLA.ttf", 18.0f));
        fonts.put("GithubButton",
                ImGui.getIO().getFonts().addFontFromFileTTF("src/main/resources/fonts/CONSOLA.ttf", 19.0F));
    }

    public ImFont getFont(String key) {
        return fonts.get(key);
    }

}
