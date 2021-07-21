package core;

import gfx.*;
import lombok.Getter;
import org.joml.Vector3f;

import java.util.*;

public abstract class Scene {

    @Getter
    private static Shader sceneShader;

    @Getter
    private static Shader gridShader;

    @Getter
    private static FrameBuffer frameBuffer;

    @Getter
    private static FirstPersonCameraController FPSCamera;

    @Getter
    private static DirectionalLight directionalLight;

    @Getter
    private static Map<String, Model> models;

    public static String SelectedModel;

    public static float[] ClearColor = {0.1f, 0.1f, 0.1f, 1.0f};


    private Scene() {
    }

    public static Model getSelectedModel() {
        return SelectedModel == null ? null : models.get(SelectedModel);
    }

    public static void initialize() {
        sceneShader = new Shader(Constants.DEFAULT_SCENE_VERTEX_SHADER_PATH, Constants.DEFAULT_SCENE_FRAGMENT_SHADER_PATH);
        gridShader = new Shader(Constants.DEFAULT_GRID_VERTEX_SHADER_PATH, Constants.DEFAULT_GRID_FRAGMENT_SHADER_PATH);

        frameBuffer = new FrameBuffer(Constants.WINDOW_DEFAULT_WIDTH, Constants.WINDOW_DEFAULT_HEIGHT);
        FPSCamera = new FirstPersonCameraController(Constants.FPS_CAMERA_DEFAULT_FOV, Constants.FPS_CAMERA_DEFAULT_ASPECT, Constants.FPS_CAMERA_DEFAULT_NEAR, Constants.FPS_CAMERA_DEFAULT_FAR);
        directionalLight = new DirectionalLight(new Vector3f(-1.f, -1.f, -1.f), new Vector3f(0.1f, 0.1f, 0.1f), new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(0.4f, 0.4f, 0.4f));
        models = new LinkedHashMap<>();
        FPSCamera.getPosition().set(0, 1, 4);
        FPSCamera.updateCamera();
        FPSCamera.updateVectors();
    }

    public static void dispose() {
        sceneShader.dispose();
        gridShader.dispose();
        frameBuffer.dispose();
    }
}
