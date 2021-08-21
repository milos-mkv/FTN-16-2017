package core;

import org.joml.Vector3f;

import java.util.Arrays;
import java.util.List;

public abstract class Constants {

    private Constants() {}

    public static final String GITHUB_URL = "https://github.com/milos-mkv/FTN-16-2017";
    public static final String DEFAULT_FONT_PATH = "src/main/resources/fonts/font.ttf";
    public static final float DEFAULT_FONT_SIZE = 21.0F;
    public static final String CONSOLE_FONT_PATH = "src/main/resources/fonts/CONSOLA.ttf";
    public static final float CONSOLE_FONT_SIZE = 14.0F;
    public static final String JAPANESE_FONT_PATH = "src/main/resources/fonts/CONSOLA.ttf";
    public static final float JAPANESE_FONT_SIZE = 19.0F;

    public static final int WINDOW_DEFAULT_WIDTH = 1480;
    public static final int WINDOW_DEFAULT_HEIGHT = 768;
    public static final int FRAMEBUFFER_WIDTH = 1920;
    public static final int FRAMEBUFFER_HEIGHT = 1280;

    public static final Vector3f WORLD_UP = new Vector3f(0, 1, 0);

    public static final String ICON_TRANSLATE ="src/main/resources/images/move.png";
    public static final String ICON_ROTATE = "src/main/resources/images/rotating.png";
    public static final String ICON_SCALE = "src/main/resources/images/resize.png";


    public static final List<String> DEFAULT_SKYBOX_FACES = Arrays.asList(
            "src/main/resources/images/right.jpg",
            "src/main/resources/images/left.jpg",
            "src/main/resources/images/top.jpg",
            "src/main/resources/images/bottom.jpg",
            "src/main/resources/images/back.jpg",
            "src/main/resources/images/front.jpg"
    );

}
