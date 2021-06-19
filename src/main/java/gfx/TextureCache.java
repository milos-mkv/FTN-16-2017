package gfx;

import java.util.HashMap;
import java.util.Map;

public class TextureCache {

    public static Map<String, Texture> loadedTextures = new HashMap<>();

    public static Texture getTexture(final String path) {
        for(String key : loadedTextures.keySet()) {
            if(key.equals(path)) {
                return loadedTextures.get(key);
            }
        }
        loadedTextures.put(path, new Texture(path));
        return loadedTextures.get(path);
    }

}
