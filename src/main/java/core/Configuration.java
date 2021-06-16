package core;

import lombok.Data;

@Data
public class Configuration {
    private String title = "Simple OpenGL Renderer";
    private int width = 1280;
    private int height = 768;
    private boolean fullScreen = false;
}
