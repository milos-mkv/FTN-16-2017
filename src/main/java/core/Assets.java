package core;

import imgui.ImFont;
import imgui.ImGui;
import utils.Disposable;

import java.util.HashMap;
import java.util.Map;

public class Assets implements Disposable {

    private static Assets assets;

    public static Assets getInstance() {
        return assets == null ? assets = new Assets() : assets;
    }

    public final Map<String, ImFont> fonts = new HashMap<>();

    private Assets() {
        fonts.put("DEFAULT_FONT",
                ImGui.getIO().getFonts().addFontFromFileTTF(Constants.DEFAULT_FONT_PATH, Constants.DEFAULT_FONT_SIZE));
        fonts.put("CONSOLE_FONT",
                ImGui.getIO().getFonts().addFontFromFileTTF(Constants.CONSOLE_FONT_PATH, Constants.CONSOLE_FONT_SIZE));
        fonts.put("CODE_FONT",
                ImGui.getIO().getFonts().addFontFromFileTTF(Constants.CONSOLE_FONT_PATH, 18.0f));
        fonts.put("SPLASH_FONT",
                ImGui.getIO().getFonts().addFontFromFileTTF(Constants.JAPANESE_FONT_PATH, Constants.JAPANESE_FONT_SIZE));
    }

    public ImFont getFont(String key) {
        return fonts.get(key);
    }

    @Override
    public void dispose() {
    }
}
