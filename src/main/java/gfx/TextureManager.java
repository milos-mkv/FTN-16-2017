package gfx;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class TextureManager {

    private static TextureManager textureManager;

    @Getter
    private final Map<String, Texture> textures;

    private TextureManager() {
        textures = new HashMap<>();
    }

    public static TextureManager getInstance() {
        if (textureManager == null) {
            textureManager = new TextureManager();
        }
        return textureManager;
    }

    public Texture getTexture(final String path) {
        for(String key : textures.keySet()) {
            if(key.equals(path)) {
                return textures.get(key);
            }
        }
        textures.put(path, new Texture(path));
        return textures.get(path);
    }

    public void dispose() {
        for(String key : textures.keySet()) {
            textures.get(key).dispose();
        }
    }

}
