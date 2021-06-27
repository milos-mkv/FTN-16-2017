package managers;

import gfx.Texture;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class TextureManager {

    protected static Map<String, Texture> textures = new HashMap<>();

    private TextureManager() {
    }

    public static Texture getTexture(String path) {
        for (var entry : textures.entrySet()) {
            if(entry.getKey().equals(path)) {
                return textures.get(entry.getKey());
            }
        }

        try {
            textures.put(path, new Texture(path));
            return textures.get(path);
        } catch (RuntimeException e) {
            Logger.getGlobal().log(Level.WARNING, e.getMessage());
            return null;
        }
    }

    public static void dispose() {
        for(var entry : textures.entrySet()) {
            if(textures.get(entry.getKey()) != null) {
                textures.get(entry.getKey()).dispose();
            }
        }
    }

}
