package managers;

import gfx.Material;
import gfx.Mesh;
import gfx.Model;
import lombok.SneakyThrows;
import org.joml.Vector3f;
import utils.Disposable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    @SneakyThrows
    public Model clone(String key) {
        Model model = loadedModels.get(key);
        ArrayList<Material> materials = new ArrayList<>();
        for(Material material: model.getMaterials()) {
            Material mat  = new Material();
            mat.setSpecularColor((Vector3f) material.getSpecularColor().clone());
            mat.setDiffuseColor((Vector3f) material.getSpecularColor().clone());
            mat.setAmbientColor((Vector3f) material.getSpecularColor().clone());
            mat.setShininess(material.getShininess());
            mat.setReflectance(material.getReflectance());
            mat.setDiffuseTexture(material.getDiffuseTexture());
            mat.setSpecularTexture(material.getSpecularTexture());
            mat.setNormalTexture(material.getNormalTexture());
            mat.setName(material.getName());
            materials.add(mat);
        }

        ArrayList<Mesh> meshes = new ArrayList<>();
        for(Mesh mesh : model.getMeshes()) {
            Mesh clone = mesh.clone();
            for(Material m : materials) {
                if(m.getName().equals(mesh.getMaterial().getName())) {
                    clone.setMaterial(m);
                }
            }
            meshes.add(clone);
        }

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
