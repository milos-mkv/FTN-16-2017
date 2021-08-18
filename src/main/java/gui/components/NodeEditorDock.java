package gui.components;

import core.Scene;
import gui.Dock;
import imgui.extension.imnodes.ImNodes;
import imgui.internal.ImGui;
import imgui.type.ImInt;

public class NodeEditorDock implements Dock {

    private Scene scene;

    public NodeEditorDock() {
        this.scene = Scene.getInstance();
    }

    @Override
    public void render() {
        ImGui.begin("Node editor");
        ImNodes.beginNodeEditor();

        

        ImNodes.beginNode(1);

        ImNodes.beginNodeTitleBar();
        ImGui.textUnformatted("Diffuse Color");
        ImNodes.endNodeTitleBar();


        ImNodes.endNode();

//        ImNodes.beginNode(2);
//        ImNodes.beginNodeTitleBar();
//        ImGui.textUnformatted("output node");
//        ImNodes.endNodeTitleBar();
//
//        ImNodes.beginInputAttribute(3);
//        ImGui.text("output pin");
//        ImNodes.endInputAttribute();
//        ImNodes.endNode();

//        ImNodes.link(1, 2, 3);

        ImNodes.endNodeEditor();

//        if (ImNodes.isLinkCreated(new ImInt(0), new ImInt(3))) {
//            System.out.println("YES");
//        }

        ImGui.end();
    }
}
