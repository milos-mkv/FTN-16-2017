package core;

import gfx.*;
import lombok.Getter;
import managers.ModelManager;
import org.joml.Vector3f;
import utils.Disposable;

public class MaterialPreviewScene implements Disposable {

    private static MaterialPreviewScene materialPreviewScene;

    public static MaterialPreviewScene getInstance() {
        return materialPreviewScene == null ? materialPreviewScene = new MaterialPreviewScene() : materialPreviewScene;
    }

    @Getter
    private final PerspectiveCamera perspectiveCamera;
    @Getter
    private final FrameBuffer frameBuffer;
    @Getter
    private final Model sphere;

    private MaterialPreviewScene() {
        perspectiveCamera = new PerspectiveCamera(45.0f, 1.0f, 0.1f, 10.0f);
        perspectiveCamera.getPosition().set(0, 0, 2.5f);
        perspectiveCamera.updateCamera();
        frameBuffer = new FrameBuffer(600, 600);
        sphere = ModelManager.getInstance().clone("MaterialSphere");
    }

    @Override
    public void dispose() {
        frameBuffer.dispose();
        sphere.dispose();
    }
}
