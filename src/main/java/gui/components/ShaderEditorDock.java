package gui.components;

import core.Assets;
import gfx.ShaderProgram;
import gui.Dock;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.ImGui;
import imgui.type.ImInt;
import imgui.type.ImString;
import managers.Console;
import managers.ShaderProgramManager;

import java.util.Map;

public class ShaderEditorDock implements Dock {

    private final ImInt selectedShaderIndex = new ImInt(0);
    private final ImInt selectedShaderTypeIndex = new ImInt(0);
    private final String[] shaderTypes = { "Vertex", "Fragment" };
    private final String[] shaders;

    private final ImString code = new ImString();

    private final ShaderProgramManager shaderProgramManager;

    public ShaderEditorDock() {
        shaderProgramManager = ShaderProgramManager.getInstance();
        shaders = new String[shaderProgramManager.getShaders().size()];

        int i = 0;
        for(Map.Entry<String, ShaderProgram> entry : shaderProgramManager.getShaders().entrySet()) {
            shaders[i++] = entry.getKey();
        }

        code.set(shaderProgramManager.get(shaders[0]).getVert().getCode(), true);
    }

    @Override
    public void render() {
        ImGui.begin("Shader Editor");
        if (ImGui.button(" Compile ")) {
            ShaderProgram program = shaderProgramManager.get(shaders[selectedShaderIndex.get()]);
            program.dispose();
            Console.log(Console.Level.WARNING, "Compiling new " + shaders[selectedShaderIndex.get()] + " program!");
            if(selectedShaderTypeIndex.get() == 0) {
                try {
                    shaderProgramManager.getShaders().put(shaders[selectedShaderIndex.get()],
                            new ShaderProgram(code.get(), program.getFrag().getCode()));
                } catch (RuntimeException e) {
                    Console.log(Console.Level.ERROR, e.getMessage());
                }
            } else {
                try {
                    shaderProgramManager.getShaders().put(shaders[selectedShaderIndex.get()],
                            new ShaderProgram(program.getVert().getCode(), code.get()));
                } catch (RuntimeException e) {
                    Console.log(Console.Level.ERROR, e.getMessage());
                }
            }
        }
        ImGui.sameLine();
        ImGui.setNextItemWidth(300);
        if(ImGui.combo("Selected Shader", selectedShaderIndex, shaders)) {
            ShaderProgram program = shaderProgramManager.get(shaders[selectedShaderIndex.get()]);
            code.set(selectedShaderTypeIndex.get() == 0
                    ? program.getVert().getCode() : program.getFrag().getCode(), true);
        }
        ImGui.sameLine();
        ImGui.setNextItemWidth(100);
        if(ImGui.combo("Shader Type", selectedShaderTypeIndex, shaderTypes)) {
            ShaderProgram program = shaderProgramManager.get(shaders[selectedShaderIndex.get()]);
            code.set(selectedShaderTypeIndex.get() == 0
                    ? program.getVert().getCode() : program.getFrag().getCode(), true);
        }

        ImGui.pushFont(Assets.getInstance().getFont("CODE_FONT"));
        ImGui.pushStyleColor(ImGuiCol.FrameBg, 0.1F, 0.1F, 0.1F, 1.0F);
        ImGui.inputTextMultiline(
                "##CODE",
                code,
                ImGui.getColumnWidth(),
                ImGui.getWindowHeight() - 75,
                ImGuiInputTextFlags.CallbackResize | ImGuiInputTextFlags.AllowTabInput);
        ImGui.popStyleColor();
        ImGui.popFont();

        ImGui.end();
    }
}
