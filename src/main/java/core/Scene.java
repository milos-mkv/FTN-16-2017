package core;

import gfx.*;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {


    public static FirstPersonCameraController FPSCamera;
    public static FrameBuffer frameBuffer;

    public static ArrayList<Model> models = new ArrayList<>();

    public static List<PointLight> pointLights = new ArrayList<>();

    public static Mesh selected;

    public static void loadModelFromFile(String file) {
        models.add(new Model(file));
    }


    public static void init() {
        frameBuffer = new FrameBuffer(Constants.FRAMEBUFFER_DEFAULT_WIDTH, Constants.FRAMEBUFFER_DEFAULT_HEIGHT);
        FPSCamera = new FirstPersonCameraController(Constants.FPS_CAMERA_DEFAULT_FOV, Constants.FPS_CAMERA_DEFAULT_ASPECT,
                Constants.FPS_CAMERA_DEFAULT_NEAR, Constants.FPS_CAMERA_DEFAULT_FAR);
        FPSCamera.position.set(0, 1, 0);
        FPSCamera.UpdateVectors();
    }


}
