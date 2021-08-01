package gui.components;

import core.Assets;
import gfx.ShaderProgram;
import gui.Dock;
import imgui.flag.ImGuiInputTextFlags;
import imgui.internal.ImGui;
import imgui.type.ImInt;
import imgui.type.ImString;
import managers.ShaderProgramManager;

import java.util.Map;

public class ShaderEditorDock implements Dock {

    private final ImInt selectedShaderIndex = new ImInt(0);
    private final ImInt selectedShaderTypeIndex = new ImInt(0);
    private final String[] shaderTypes = { "Vertex", "Fragment" };
    private final String[] shaders;

    private final ImString code = new ImString();

    public ShaderEditorDock() {
        shaders = new String[ShaderProgramManager.getInstance().getShaders().size()];
        int i = 0;
        for(Map.Entry<String, ShaderProgram> entry : ShaderProgramManager.getInstance().getShaders().entrySet()) {
            shaders[i] = entry.getKey();
            System.out.println(entry.getKey());
            i++;
        }
        ShaderProgram program = ShaderProgramManager.getInstance().get(shaders[0]);
        code.set(program.getVert().getCode(), true);
    }

    @Override
    public void render() {
        int currentIndex = selectedShaderIndex.get();
        int currentTypeIndex = selectedShaderTypeIndex.get();
        ImGui.begin("Shader Editor");
        if (ImGui.button(" Compile ")) {

        }
        ImGui.sameLine();
        ImGui.setNextItemWidth(300);
        ImGui.combo("Selected Shader", selectedShaderIndex, shaders);
        ImGui.sameLine();
        ImGui.setNextItemWidth(100);
        ImGui.combo("Shader Type", selectedShaderTypeIndex, shaderTypes);
        ImGui.pushFont(Assets.Fonts.get("CODE_FONT"));
        ImGui.inputTextMultiline(
                "##CODE",
                code,
                ImGui.getColumnWidth(),
                ImGui.getWindowHeight() - 75,
                ImGuiInputTextFlags.CallbackResize | ImGuiInputTextFlags.AllowTabInput);
        ImGui.popFont();
        ImGui.end();

        if(currentIndex != selectedShaderIndex.get()) {
            ShaderProgram program = ShaderProgramManager.getInstance().get(shaders[selectedShaderIndex.get()]);
            if(selectedShaderTypeIndex.get() == 0) {
                code.set(program.getVert().getCode(), true);
            } else {
                code.set(program.getFrag().getCode(), true);
            }
        }
        if(currentTypeIndex != selectedShaderTypeIndex.get()) {
            if(selectedShaderTypeIndex.get() == 0) {
                code.set(ShaderProgramManager.getInstance().get(shaders[selectedShaderIndex.get()]).getVert().getCode(), true);
            } else {
                code.set(ShaderProgramManager.getInstance().get(shaders[selectedShaderIndex.get()]).getFrag().getCode(), true);
            }
        }

    }
}
