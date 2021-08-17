package core;

import gfx.*;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import utils.Disposable;

import java.util.*;

public class Scene implements Disposable {

    private static Scene scene;

    public static Scene getInstance() {
        return scene == null ? scene = new Scene() : scene;
    }

    @Getter
    private final FrameBuffer frameBuffer;

    @Getter
    private final FirstPersonCameraController camera;

    @Getter
    private final DirectionalLight directionalLight;

    @Getter
    private final Map<String, Model> models = new LinkedHashMap<>();

    @Setter
    private String selectedModel;

    @Setter
    private String selectedMesh;

    public Mesh getSelectedMesh() {
        if (selectedMesh != null) {
            for (Map.Entry<String, Mesh> entry : getSelectedModel().getMeshes().entrySet()) {
                if (selectedMesh.equals(entry.getValue().getName()))
                    return entry.getValue();
            }
        }
        return null;
    }

    public static float[] ClearColor = {0.1f, 0.1f, 0.1f, 1.0f};

    public Model getSelectedModel() {
        return selectedModel == null ? null : models.get(selectedModel);
    }

    public FrameBuffer selectFrameBuffer;
    public MSFrameBuffer msFrameBuffer;

    private Scene() {
        frameBuffer = new FrameBuffer(Constants.FRAMEBUFFER_WIDTH, Constants.FRAMEBUFFER_HEIGHT);
        directionalLight = new DirectionalLight(
                new Vector3f(-1.f, -1.f, -1.f),
                new Vector3f(0.1f, 0.1f, 0.1f),
                new Vector3f(1.0f, 1.0f, 1.0f),
                new Vector3f(0.4f, 0.4f, 0.4f));
        camera = new FirstPersonCameraController(45.0F, 1280.F / 768.F, 0.1F, 100.0F);
        camera.getPosition().set(0, 1, 4);
        camera.updateCamera();
        camera.updateVectors();
        selectFrameBuffer = new FrameBuffer(Constants.FRAMEBUFFER_WIDTH, Constants.FRAMEBUFFER_HEIGHT, true);
        msFrameBuffer = new MSFrameBuffer(Constants.FRAMEBUFFER_WIDTH, Constants.FRAMEBUFFER_HEIGHT);
    }


    @Override
    public void dispose() {
        frameBuffer.dispose();
        selectFrameBuffer.dispose();
    }
}
