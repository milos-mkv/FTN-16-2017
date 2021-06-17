package gfx;

import java.util.HashMap;

public class TextureCache {

    private static TextureCache textureCache;

    public HashMap<String, Texture> loadedTextures;

    public static TextureCache getInstance() {
        if (textureCache == null) {
            textureCache = new TextureCache();
        }
        return textureCache;
    }

    private TextureCache() {
        loadedTextures = new HashMap<>();
    }
}
