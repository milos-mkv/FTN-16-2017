package gui.components;

import core.Assets;
import core.Constants;
import core.Settings;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import managers.TextureManager;
import utils.Renderable;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class SplashModal implements Renderable {

    @Override
    public void render() {
        if (!Settings.ShowSplashModal.get()) {
            return;
        }
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0F, 0.0F);
        ImGui.begin("Hatsune Miku", Settings.ShowSplashModal,
            ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize);

        ImGui.setWindowSize(600, 405);
        ImGui.image(Objects.requireNonNull(TextureManager.getInstance().getTexture(Constants.SPLASH_IMAGE_PATH)).getId(),
            ImGui.getWindowWidth(), ImGui.getWindowHeight() - 30);

        ImGui.setCursorPos(130, 290);
        ImGui.pushFont(Assets.getInstance().getFont("SPLASH_FONT"));

        if (ImGui.button(" Github ")) {
            try {
                Desktop.getDesktop().browse(new URI(Constants.GITHUB_URL));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }

        ImGui.popFont();
        ImGui.end();
        ImGui.popStyleVar();
    }
}
