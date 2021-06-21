package core;

import org.joml.Vector4f;

public abstract class Constants {

    public static final int FRAMEBUFFER_DEFAULT_WIDTH = 1280;
    public static final int FRAMEBUFFER_DEFAULT_HEIGHT = 769;

    public static final float FPS_CAMERA_DEFAULT_FOV = 45.0F;
    public static final float FPS_CAMERA_DEFAULT_ASPECT = 1280.F / 769.F;
    public static final float FPS_CAMERA_DEFAULT_NEAR = 0.1F;
    public static final float FPS_CAMERA_DEFAULT_FAR = 100.0F;

    public static final String SCENE_GRID_VERTEX_SHADER_PATH = "src/main/resources/shaders/grid.vert";
    public static final String SCENE_GRID_FRAGMENT_SHADER_PATH = "src/main/resources/shaders/grid.frag";
    public static final String SCENE_VERTEX_SHADER_PATH = "src/main/resources/shaders/shader.vert";
    public static final String SCENE_FRAGMENT_SHADER_PATH = "src/main/resources/shaders/shader.frag";

    public static final Vector4f DEFAULT_COLOR = new Vector4f(1, 1, 1, 1);

    public static final String[] DEFAULT_MESHES_PATHS = {
            "src/main/resources/meshes/cone.obj",
            "src/main/resources/meshes/cube.obj",
            "src/main/resources/meshes/cylinder.obj",
            "src/main/resources/meshes/grid.obj",
            "src/main/resources/meshes/icosphere.obj",
            "src/main/resources/meshes/monkey.obj",
            "src/main/resources/meshes/plane.obj",
            "src/main/resources/meshes/sphere.obj",
            "src/main/resources/meshes/torus.obj"
    };


}
