package gui.components;

import core.Settings;
import core.Window;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import lombok.SneakyThrows;
import managers.TextureManager;
import org.lwjgl.glfw.GLFW;
import utils.Renderable;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class SplashModal implements Renderable {


    public SplashModal() {

    }

    @Override
    public void render() {
        if(!Settings.ShowSplashModal.get()) {
            return;
        }
        ImGui.pushStyleColor(ImGuiCol.Tab, 0.95f, 0.40f, 0.2f, 1.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0F, 0.0F);
        ImGui.begin("Misaka Railgun", Settings.ShowSplashModal,
                ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize );
        ImGui.setWindowSize(520, 300);
        ImGui.image(Objects.requireNonNull(TextureManager.getTexture("src/main/resources/images/Untitled.png")).getId(),
                ImGui.getWindowWidth(), ImGui.getWindowHeight() - 30);
        ImGui.setCursorPos(260, 225);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.4f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.7f, 0.4f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.4f, 0.1f, 1.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 5);
        ImGui.pushFont(Window.japFont);
        if(ImGui.button(" Github ")) {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/milos-mkv/FTN-16-2017"));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        ImGui.popFont();
        ImGui.popStyleVar();
        ImGui.popStyleColor(3);
        ImGui.end();
        ImGui.popStyleVar();
        ImGui.popStyleColor();


    }
}
