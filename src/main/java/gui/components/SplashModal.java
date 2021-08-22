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

public class SplashModal implements Renderable {

    private final int flags;
    private final TextureManager textureManager;
    private final Assets assets;

    public SplashModal() {
        this.flags = ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoCollapse |
                     ImGuiWindowFlags.NoResize;
        this.textureManager = TextureManager.getInstance();
        this.assets = Assets.getInstance();
    }

    @Override
    public void render() {
        if (!Settings.ShowSplashModal.get()) {
            return;
        }

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0F, 0.0F);
        ImGui.pushStyleColor(ImGuiCol.WindowBg, 0.1F, 0.1f, 0.1F, 1.0F);
        ImGui.begin("Misaka Railgun", Settings.ShowSplashModal, flags);

        ImGui.setWindowSize(600, 405);
        ImGui.image(textureManager.getTexture("src/main/resources/images/splash.png").getId(),
                ImGui.getWindowWidth(), ImGui.getWindowHeight() - 30);

        ImGui.setCursorPos(370, 320);
        ImGui.pushFont(assets.getFont("GithubButton"));

        if (ImGui.button(" Github ")) {
            try {
                Desktop.getDesktop().browse(new URI(Constants.GITHUB_URL));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }

        ImGui.popFont();
        ImGui.end();
        ImGui.popStyleColor();
        ImGui.popStyleVar();
    }
}
