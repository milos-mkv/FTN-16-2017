package core;

import org.joml.Vector3f;

import java.util.Arrays;
import java.util.List;

public abstract class Constants {

    private Constants() {}

    public static final String IMGUI_DEFAULT_FONT_PATH = "src/main/resources/fonts/font.ttf";
    public static final float IMGUI_DEFAULT_FONT_SIZE = 21.0F;
    public static final int WINDOW_DEFAULT_WIDTH = 1280;
    public static final int WINDOW_DEFAULT_HEIGHT = 768;
    public static final String DEFAULT_SCENE_VERTEX_SHADER_PATH = "src/main/resources/shaders/shader.vert";
    public static final String DEFAULT_SCENE_FRAGMENT_SHADER_PATH = "src/main/resources/shaders/shader.frag";
    public static final String DEFAULT_GRID_VERTEX_SHADER_PATH = "src/main/resources/shaders/grid.frag";
    public static final String DEFAULT_GRID_FRAGMENT_SHADER_PATH = "src/main/resources/shaders/grid.frag";
    public static final Vector3f WORLD_UP = new Vector3f(0, 1, 0);
    public static final float FPS_CAMERA_DEFAULT_FOV = 45.0F;
    public static final float FPS_CAMERA_DEFAULT_ASPECT = 1280.F / 768.F;
    public static final float FPS_CAMERA_DEFAULT_NEAR = 0.1F;
    public static final float FPS_CAMERA_DEFAULT_FAR = 100.0F;

    public static final Vector3f DEFAULT_COLOR = new Vector3f(1, 1, 1);
    public static final String DEFAULT_SKYBOX_VERTEX_SHADER_PATH = "src/main/resources/shaders/skybox.vert";
    public static final String DEFAULT_SKYBOX_FRAGMENT_SHADER_PATH = "src/main/resources/shaders/skybox.frag";

    public static final String ICON_TRANSLATE ="src/main/resources/images/move.png";
    public static final String ICON_ROTATE = "src/main/resources/images/rotation.png";
    public static final String ICON_SCALE = "src/main/resources/images/scale.png";


    public static final List<String> DEFAULT_SKYBOX_FACES = Arrays.asList(
            "src/main/resources/images/right.jpg",
            "src/main/resources/images/left.jpg",
            "src/main/resources/images/top.jpg",
            "src/main/resources/images/bottom.jpg",
            "src/main/resources/images/back.jpg",
            "src/main/resources/images/front.jpg"
    );

}
