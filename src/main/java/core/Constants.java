package core;

import org.joml.Vector3f;

public abstract class Constants {

    private Constants() {}

    public static final String GITHUB_URL = "https://github.com/milos-mkv/FTN-16-2017";

    public static final int WINDOW_DEFAULT_WIDTH  = 1480;
    public static final int WINDOW_DEFAULT_HEIGHT =  768;
    public static final int FRAMEBUFFER_WIDTH     = 1920;
    public static final int FRAMEBUFFER_HEIGHT    = 1280;

    public static final Vector3f WORLD_UP = new Vector3f(0, 1, 0);

}
