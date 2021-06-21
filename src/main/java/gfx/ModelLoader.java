package gfx;

import core.Constants;
import lombok.SneakyThrows;

import java.util.*;

public abstract class ModelLoader {

    public static Map<String, Model> LoadedModels = new HashMap<>();

    public static void init() {
        for (String resourcePath : Constants.DEFAULT_MESHES_PATHS) {
            LoadedModels.put(resourcePath, new Model(resourcePath));
        }
    }

    @SneakyThrows
    public static Model getModel(String resourcePath) {
        for (String key : LoadedModels.keySet()) {
            if(key == resourcePath) {
                return LoadedModels.get(key).clone();
            }
        }
        LoadedModels.put(resourcePath, new Model(resourcePath));
        return LoadedModels.get(resourcePath);
    }

}
