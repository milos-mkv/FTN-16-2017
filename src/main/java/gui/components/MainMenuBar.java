package gui.components;

import utils.Renderable;
import core.Constants;
import core.Scene;
import core.Settings;
import core.Window;
import exceptions.InvalidDocumentException;
import gfx.Model;
import imgui.ImGui;
import managers.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.util.nfd.NativeFileDialog;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL30C.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30C.glBindFramebuffer;

public class MainMenuBar implements Renderable {

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
            ImGui.endMenu();
        }
    }

    private void renderRenderMenu() {
        if (ImGui.beginMenu("Render")) {
            if (ImGui.menuItem("Render Image")) {
                executeRenderImage();
            }
            ImGui.endMenu();
        }
    }

    private void openImportModelFailedDialog() {
        try {
            var pointerBuffer = PointerBuffer.allocateDirect(1);
            NativeFileDialog.NFD_OpenDialog((CharSequence) null, null, pointerBuffer);

            var model = new Model(pointerBuffer.getStringASCII().replace("\\", "/"));
            Scene.getModels().put("Model " + Scene.getModels().size(), model);
            Logger.log(Logger.Level.INFO, "Model successfully loaded: " + model.getPath());
        } catch (InvalidDocumentException e) {
            Logger.log(Logger.Level.ERROR, e.getMessage());
        } catch (Exception e) { }
    }

    private void executeRenderImage() {
        glBindFramebuffer(GL_FRAMEBUFFER, Scene.getFrameBuffer().getId());
        glPixelStorei(GL_PACK_ALIGNMENT, 1);
        var data = BufferUtils.createByteBuffer(Constants.WINDOW_DEFAULT_WIDTH * Constants.WINDOW_DEFAULT_HEIGHT * 3);
        glReadPixels(0, 0, Constants.WINDOW_DEFAULT_WIDTH, Constants.WINDOW_DEFAULT_HEIGHT, GL_RGB, GL_UNSIGNED_BYTE, data);

        STBImageWrite.nstbi_flip_vertically_on_write(1);
        STBImageWrite.stbi_write_jpg("test.jpg", Constants.WINDOW_DEFAULT_WIDTH, Constants.WINDOW_DEFAULT_HEIGHT, 3, data, Constants.WINDOW_DEFAULT_WIDTH * 3);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
}
