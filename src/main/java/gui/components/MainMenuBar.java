package gui.components;

import core.Constants;
import core.Scene;
import core.Settings;
import core.Window;
import gui.GUIControls;
import imgui.ImGui;
import managers.Console;
import managers.ModelManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.stb.STBImageWrite;
import utils.Renderable;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL30C.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30C.glBindFramebuffer;

public class MainMenuBar implements Renderable {

    private final Scene scene;
    private final ModelManager modelManager;

    public MainMenuBar() {
        this.scene = Scene.getInstance();
        this.modelManager = ModelManager.getInstance();
    }

    @Override
    public void render() {
        if (ImGui.beginMainMenuBar()) {
            renderFileMenu();
            renderViewMenu();
            renderRenderMenu();
            ImGui.endMainMenuBar();
        }
    }

    private void renderFileMenu() {
        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Import Model")) {
                openImportModelFailedDialog();
            }
            ImGui.separator();
            if (ImGui.menuItem("Splash")) {
                Settings.ShowSplashModal.set(true);
            }
            if (ImGui.menuItem("Exit", "Ctrl+Q")) {
                GLFW.glfwSetWindowShouldClose(Window.getHandle(), true);
            }
            ImGui.endMenu();
        }
    }

    private void renderViewMenu() {
        if (ImGui.beginMenu("View")) {
            if (ImGui.menuItem("Toggle Console Dock")) {
                Settings.ShowConsoleDock.set(!Settings.ShowConsoleDock.get());
            }
            if (ImGui.menuItem("Toggle Scene Properties Dock")) {
                Settings.ShowScenePropertiesDock.set(!Settings.ShowScenePropertiesDock.get());
            }
            if (ImGui.menuItem("Toggle Model Properties Dock")) {
                Settings.ShowModelPropertiesDock.set(!Settings.ShowModelPropertiesDock.get());
            }
            if (ImGui.menuItem("Toggle Skybox Properties Dock")) {
                Settings.ShowSkyboxPropertiesDock.set(!Settings.ShowSkyboxPropertiesDock.get());
            }
            if (ImGui.menuItem("Toggle Texture Preview Dock")) {
                Settings.ShowTexturePreviewDock.set(!Settings.ShowTexturePreviewDock.get());
            }
            ImGui.endMenu();
        }
    }

    private void renderRenderMenu() {
        if (ImGui.beginMenu("Render")) {
            if (ImGui.menuItem("Toggle SkyBox")) {
                Settings.ToggleSkyBox.set(!Settings.ToggleSkyBox.get());
            }
            if (ImGui.menuItem("Toggle Grid")) {
                Settings.ToggleGrid.set(!Settings.ToggleGrid.get());
            }
            if (ImGui.menuItem("Render Image")) {
                executeRenderImage();
            }
            ImGui.endMenu();
        }
    }

    private void openImportModelFailedDialog() {
        String path = GUIControls.controlOpenFileDialog();

        if(path == null) {
            return;
        }

        try {
            var model = modelManager.clone(path);
            String key = "Model " + scene.nextModelIndex++;
            scene.getModels().put(key, model);
            scene.setSelectedModel(key);
            Console.log(Console.Level.INFO, "Model successfully loaded: " + model.getPath());
        } catch (RuntimeException e) {
            Console.log(Console.Level.ERROR, "Failed to load model: " + path);
        }
    }

    private void executeRenderImage() {
        glBindFramebuffer(GL_FRAMEBUFFER, scene.getFrameBuffer().getId());
        glPixelStorei(GL_PACK_ALIGNMENT, 1);
        var data = BufferUtils.createByteBuffer(Constants.FRAMEBUFFER_WIDTH * Constants.FRAMEBUFFER_HEIGHT * 3);
        glReadPixels(0, 0, Constants.FRAMEBUFFER_WIDTH, Constants.FRAMEBUFFER_HEIGHT, GL_RGB, GL_UNSIGNED_BYTE, data);

        STBImageWrite.nstbi_flip_vertically_on_write(1);
        STBImageWrite.stbi_write_jpg("image.jpg", Constants.FRAMEBUFFER_WIDTH, Constants.FRAMEBUFFER_HEIGHT,
                3, data, Constants.FRAMEBUFFER_WIDTH * 3);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        Console.log(Console.Level.INFO, "Scene rendered successfully.");
    }
}
