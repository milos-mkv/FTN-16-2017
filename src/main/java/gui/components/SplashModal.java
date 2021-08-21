/**
 * @file SplashModal.java
 * @author Milos Milicevic (milosh.mkv@gmail.com)
 * @copyright Copyright (c) 2021
 * <p>
 * Distributed under the MIT software license, see the accompanying file LICENCE or https://opensource.org/licenses/MIT.
 */

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

    public SplashModal() {
        this.flags = ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoCollapse |
                     ImGuiWindowFlags.NoResize;
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
        ImGui.image(TextureManager.getInstance().getTexture("src/main/resources/images/splash.png").getId(),
                ImGui.getWindowWidth(), ImGui.getWindowHeight() - 30);

        ImGui.setCursorPos(370, 320);
        ImGui.pushFont(Assets.getInstance().getFont("GithubButton"));

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
