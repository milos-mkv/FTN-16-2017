package managers;

import gfx.Texture;

import java.util.HashMap;
import java.util.Map;

public abstract class TextureManager {

    private static Map<String, Texture> textures;

    public static void initialize() {
        textures = new HashMap<>();
    }

    public static Texture getTexture(String path) {
        for (String key : textures.keySet()) {
            if(key.equals(path)) {
                return textures.get(key);
            }
        }
        try {
            textures.put(path, new Texture(path));
            return textures.get(path);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return null;
        }

    }

    public static void dispose() {
        for(String key : textures.keySet()) {
            if(textures.get(key) != null) {
                textures.get(key).dispose();
            }
        }
    }

}
