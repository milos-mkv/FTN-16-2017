package managers;

import gfx.ShaderProgram;
import lombok.Getter;
import utils.Disposable;

import java.util.LinkedHashMap;
import java.util.Map;

import static utils.Utils.readFromFile;

public class ShaderProgramManager implements Disposable {

    private static ShaderProgramManager shaderProgramManager;

    public static ShaderProgramManager getInstance() {
        return shaderProgramManager == null ? shaderProgramManager = new ShaderProgramManager() : shaderProgramManager;
    }

    @Getter
    private final Map<String, ShaderProgram> shaders = new LinkedHashMap<>();

    private ShaderProgramManager() {
        shaders.put("SCENE SHADER", new ShaderProgram(
                readFromFile("src/main/resources/shaders/shader.vert"), readFromFile("src/main/resources/shaders/shader.frag")
        ));
        shaders.put("GRID SHADER", new ShaderProgram(
                readFromFile("src/main/resources/shaders/newgrid.vert"), readFromFile("src/main/resources/shaders/newgrid.frag"
        )));
        shaders.put("SHADOW SHADER", new ShaderProgram(
                readFromFile("src/main/resources/shaders/shadow.vert"), readFromFile("src/main/resources/shaders/shadow.frag")
        ));
        shaders.put("SKYBOX SHADER", new ShaderProgram(
                readFromFile("src/main/resources/shaders/skybox.vert"), readFromFile("src/main/resources/shaders/skybox.frag")
        ));
        shaders.put("BORDER SHADER", new ShaderProgram(
                readFromFile("src/main/resources/shaders/border.vert"), readFromFile("src/main/resources/shaders/border.frag")
        ));
    }

    public ShaderProgram get(String shader) {
        return shaders.get(shader);
    }
    
    public void dispose() {
        shaders.forEach((key, value) -> value.dispose());
    }
}
