package managers;

import gfx.Material;
import gfx.Mesh;
import gfx.Model;
import utils.Disposable;

import java.util.*;
import java.util.stream.Collectors;

public class ModelManager implements Disposable {

    private static ModelManager modelManager;

    public static ModelManager getInstance() {
        return modelManager == null ? modelManager = new ModelManager() : modelManager;
    }

    private final Map<String, Model> loadedModels = new HashMap<>();

    private ModelManager() {
        loadedModels.put("Cone", new Model("src/main/resources/meshes/cone.obj"));
        loadedModels.put("Cube", new Model("src/main/resources/meshes/cube.obj"));
        loadedModels.put("Cylinder", new Model("src/main/resources/meshes/cylinder.obj"));
        loadedModels.put("Sphere", new Model("src/main/resources/meshes/sphere.obj"));
        loadedModels.put("Grid", new Model("src/main/resources/meshes/grid.obj"));
        loadedModels.put("Icosphere", new Model("src/main/resources/meshes/icosphere.obj"));
        loadedModels.put("Monkey", new Model("src/main/resources/meshes/monkey.obj"));
        loadedModels.put("Torus", new Model("src/main/resources/meshes/torus.obj"));
    }

    public Model clone(String key) {
        loadedModels.computeIfAbsent(key, Model::new);
        return this.clone(loadedModels.get(key));
    }

    public Model clone(Model model) {
        Map<String, Material> materials = model.getMaterials()
                .entrySet()
                .stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), Material.clone(entry.getValue())))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        Map<String, Mesh> meshes = model.getMeshes()
                .entrySet()
                .stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(),
                        Mesh.clone(entry.getValue(), materials.get(entry.getValue().getMaterial().getName()))))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        return new Model(meshes, materials);
    }

    @Override
    public void dispose() {
        loadedModels.forEach((key,value) -> {
            if(value != null) {
                value.dispose();
            }
        });
    }
}
