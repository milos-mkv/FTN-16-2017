package gui.components;

import core.Assets;
import core.Scene;
import gfx.Mesh;
import gfx.Model;
import imgui.ImGui;
import imgui.type.ImInt;
import utils.Renderable;

public class MeshPropertiesPopup implements Renderable {

    private Scene scene;
    private ImInt selectedMaterialIndex = new ImInt(0);

    public MeshPropertiesPopup() {
        scene = Scene.getInstance();
    }

    @Override
    public void render() {
        if (scene.getSelectedMesh() == null) {
            return;
        }
        Model model = scene.getSelectedModel();
        Mesh mesh = scene.getSelectedMesh();

        String[] materials = new String[model.getMaterials().size()];
        for (int i = 0; i < materials.length; i++) {
            materials[i] = model.getMaterials().get(i).getName();
            if (mesh.getMaterial().getName().equals(materials[i])) {
                selectedMaterialIndex.set(i);
            }
        }
        ImGui.pushFont(Assets.getInstance().getFont("CODE_FONT"));
        if (ImGui.beginPopup("Mesh Properties")) {
            ImGui.text("Mesh name: " + mesh.getName());
            ImGui.text("Selected material");
            ImGui.setNextItemWidth(200);
            if (ImGui.combo("##Used material", selectedMaterialIndex, materials)) {
                model.getMaterials().forEach(material -> {
                    if (material.getName().equals(materials[selectedMaterialIndex.get()])) {
                        mesh.setMaterial(material);
                    }
                });
            }
            ImGui.text("Number of vertices: " + mesh.getVertices().size());
            ImGui.text("Number of indices: " + mesh.getIndices().size());
            if(ImGui.button("Delete this mesh")) {
            }
            ImGui.endPopup();
        }
        ImGui.popFont();
    }
}
