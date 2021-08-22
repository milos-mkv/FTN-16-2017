package gui.components;

import core.Scene;
import gfx.Mesh;
import gfx.Model;
import imgui.ImGui;
import imgui.type.ImInt;
import managers.Console;
import utils.Renderable;

import java.util.Arrays;
import java.util.List;

public class MeshPropertiesPopup implements Renderable {

    private final Scene scene;
    private final ImInt selectedMaterialIndex = new ImInt(0);

    public MeshPropertiesPopup() {
        scene = Scene.getInstance();
    }

    @Override
    public void render() {
        if (scene.getSelectedMesh() == null) {
            return;
        }

        final Model model = scene.getSelectedModel();
        final Mesh  mesh  = scene.getSelectedMesh();

        List<String> materials = Arrays.asList(model.getMaterials().keySet().toArray(new String[0]));
        selectedMaterialIndex.set(materials.indexOf(mesh.getMaterial().getName()));

        if (ImGui.beginPopup("Mesh Properties")) {
            ImGui.textColored(1.0F, 0.5F, 0.2F, 1.0F, "Mesh name: " + mesh.getName());
            ImGui.separator();
            ImGui.text("Selected material");

            ImGui.setNextItemWidth(200);
            if (ImGui.combo("##Used material", selectedMaterialIndex, materials.toArray(new String[0]))) {
                mesh.setMaterial(model.getMaterials().get(materials.get(selectedMaterialIndex.get())));
            }

            ImGui.text("Number of vertices: " + mesh.getVertices().size());
            ImGui.text("Number of indices: "  + mesh.getIndices().size());
            if (ImGui.button("Delete this mesh")) {
                Console.log(Console.Level.WARNING, "Deleted mesh " + mesh.getName() + " @ " + System.identityHashCode(mesh));
                model.getMeshes().remove(mesh.getName());
            }
            ImGui.endPopup();
        }
    }
}
