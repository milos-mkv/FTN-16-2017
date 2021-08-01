package managers;

import exceptions.InvalidDocumentException;
import gfx.Texture;
import utils.Disposable;

import java.util.HashMap;
import java.util.Map;

public class TextureManager implements Disposable {

    private static TextureManager textureManager;

    public static TextureManager getInstance() {
        return textureManager == null ? textureManager = new TextureManager() : textureManager;
    }

    protected Map<String, Texture> textures = new HashMap<>();

    private TextureManager() { /* Empty */ }

    public Texture getTexture(String path) {
        for (var entry : textures.entrySet()) {
            if (entry.getKey().equals(path)) {
                return textures.get(entry.getKey());
            }
        }

        try {
            textures.put(path, new Texture(path));
            return textures.get(path);
        } catch (InvalidDocumentException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void dispose() {
        textures.forEach((key, value) -> {
            if(value != null) {
                value.dispose();
            }
        });
    }

}
