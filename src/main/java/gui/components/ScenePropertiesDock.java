package gui.components;

import core.Scene;
import core.Settings;
import gfx.Model;
import gui.Dock;
import gui.GUIControls;
import imgui.ImGui;
import managers.Console;
import managers.TextureManager;
import org.lwjgl.glfw.GLFW;

import static gui.GUIControls.*;
import static org.lwjgl.opengl.GL11.*;

public class ScenePropertiesDock implements Dock {

    private final Scene scene;
    private final TextureManager textureManager;
    private final MeshPropertiesPopup meshPropertiesPopup;
    private String modelToRemove = null;

    private final float[] lineWidth   = {1.0F};
    private final float[] cameraSpeed = {5.0F};

    public ScenePropertiesDock() {
        this.scene = Scene.getInstance();
        this.textureManager = TextureManager.getInstance();
        this.meshPropertiesPopup = new MeshPropertiesPopup();
    }

    @Override
    public void render() {
        if (!Settings.ShowScenePropertiesDock.get()) {
            return;
        }

        ImGui.begin("Scene Properties", Settings.ShowScenePropertiesDock);

        renderSceneItems();
        renderClearColor();
        renderDirectinalLightProperties();
        renderOpenGLFunctions();

        ImGui.end();
    }

    private void renderSceneItems() {
        if (ImGui.collapsingHeader("Scene Items")) {
            scene.getModels().forEach((name, model) -> {
                ImGui.image(textureManager.getTexture("src/main/resources/images/model_icon.png").getId(), 20, 20);
                ImGui.sameLine();
                renderModelTreeNode(name, model);
            });
            if (modelToRemove != null) {
                Console.log(Console.Level.WARNING, "Deleted " + modelToRemove + " @ " + System.identityHashCode(scene.getModels().get(modelToRemove)));
                scene.getModels().remove(modelToRemove);
                scene.setSelectedModel(null);
                modelToRemove = null;
            }
        }
    }

    private void renderModelTreeNode(String modelName, Model model) {
        if (ImGui.treeNode(modelName)) {
            ImGui.separator();
            ImGui.text("Number of meshes: " + model.getMeshes().size());
            ImGui.sameLine();
            if (ImGui.button(" Delete ")) {
                modelToRemove = modelName;
            }
            ImGui.separator();
            model.getMeshes().forEach((meshName, mesh) -> {
                ImGui.image(textureManager.getTexture("src/main/resources/images/mesh_icon.png").getId(), 20, 20);
                ImGui.sameLine();
                if (ImGui.selectable(meshName)) {
                    scene.setSelectedModel(modelName);
                    scene.setSelectedMesh(meshName);
                    ImGui.openPopup("Mesh Properties");
                }
            });
            meshPropertiesPopup.render();
            ImGui.treePop();
        }
    }

    private void renderClearColor() {
        if (ImGui.collapsingHeader("Clear Color")) {
            ImGui.colorPicker4("##ClearColor", Scene.ClearColor);
        }
    }

    private void renderDirectinalLightProperties() {
        if (ImGui.collapsingHeader("Directional Light")) {
            if (ImGui.checkbox("Enable Directional Light", Settings.EnableDirectionalLight)) {
                Console.log(Console.Level.INFO, Settings.EnableDirectionalLight.get() ? "Directional light enabled." :
                        "Directional light disabled.");
            }
            controlDragFloat3("Direction", scene.getDirectionalLight().getDirection(), -1, 1);
            controlRGB("Ambient", scene.getDirectionalLight().getAmbient());
            controlRGB("Diffuse", scene.getDirectionalLight().getDiffuse());
            controlRGB("Specular", scene.getDirectionalLight().getSpecular());
        }
    }

    private synchronized void renderOpenGLFunctions() {
        if (ImGui.collapsingHeader("OpenGL Functions")) {
            if (ImGui.checkbox("Enable shadows", Settings.EnableShadows)) {
                Console.log(Console.Level.INFO, Settings.EnableShadows.get() ? "Shadows enabled." :
                        "Shadows disabled.");
            }
            if (ImGui.checkbox("Enable line polygon mode", Settings.EnableLinePolygonMode)) {
                Console.log(Console.Level.INFO, Settings.EnableLinePolygonMode.get() ? "Line polygon mode enabled." :
                        "Line polygon mode disabled.");
            }
            if (ImGui.checkbox("Enable face culling", Settings.EnableFaceCulling)) {
                Console.log(Console.Level.INFO, Settings.EnableFaceCulling.get() ? "Face culling enabled." :
                        "Face culling disabled.");
            }
            if (ImGui.checkbox("Enable MSAA", Settings.EnableMSAA)) {
                Console.log(Console.Level.INFO, Settings.EnableMSAA.get() ? "Multisample anti aliasing enabled." :
                        "Multisample anti aliasing disabled.");
            }
            if (ImGui.checkbox("Enable skybox", Settings.ToggleSkyBox)) {
                Console.log(Console.Level.INFO, Settings.ToggleSkyBox.get() ? "Skybox enabled." :
                        "Skybox disabled.");
            }
            if (ImGui.checkbox("Enable grid", Settings.ToggleGrid)) {
                Console.log(Console.Level.INFO, Settings.ToggleGrid.get() ? "Grid enabled." :
                        "Grid disabled.");
            }
            if (ImGui.checkbox("Cap FPS to 60", Settings.CapFPS)) {
                GLFW.glfwSwapInterval(Settings.CapFPS.get() ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
            }

            ImGui.text("Line width");
            ImGui.setNextItemWidth(ImGui.getColumnWidth());
            if (ImGui.dragFloat("##Line width", lineWidth, 0.1f, 1.0f, 10.f)) {
                glLineWidth(lineWidth[0]);
                Settings.GLLineWidth = lineWidth[0];
            }

            ImGui.text("Camera speed");
            ImGui.setNextItemWidth(ImGui.getColumnWidth());
            if (ImGui.dragFloat("##Camera speed", cameraSpeed, 0.1f, 1.0f, 10.f)) {
                scene.getCamera().setSpeed(cameraSpeed[0]);
            }

            if (ImGui.checkbox("Enable selector border", Settings.EnableSelectorBorder)) {
                Console.log(Console.Level.INFO, Settings.EnableSelectorBorder.get() ? "Selector border enabled." :
                        "Selector border disabled.");
            }

            ImGui.setNextItemWidth(ImGui.getColumnWidth());
            GUIControls.controlRGB("Selector color", Settings.SelectorColor);
        }
    }

}
