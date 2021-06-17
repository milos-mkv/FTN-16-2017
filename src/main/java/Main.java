import core.Application;
import gfx.FirstPersonCameraController;
import gfx.FrameBuffer;
import gfx.Model;
import gfx.Shader;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import lombok.SneakyThrows;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL32;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static utils.Utils.ReadFromFile;

public class Main extends Application {

    public static void main(String[] args) {
        launch(new Main());
    }

    private Shader shader, gridShader;
    private FrameBuffer fb;
    private FirstPersonCameraController controller;

    int vao, vbo;

    private Model MOD;


    @SneakyThrows
    @Override
    public void preProcess() {
        GL32.glEnable(GL32.GL_DEPTH_TEST);
        Vector3f a = new Vector3f(1, 1, 1);
        Vector3f b = (Vector3f) a.clone();
        b.x = 2;
        System.out.println(a.x);
        gridShader = new Shader(ReadFromFile("src/main/resources/shaders/grid.vert"), ReadFromFile("src/main/resources/shaders/grid.frag"));
        shader = new Shader(ReadFromFile("src/main/resources/shaders/shader.vert"), ReadFromFile("src/main/resources/shaders/shader.frag"));
        fb = new FrameBuffer(1280, 769);
        controller = new FirstPersonCameraController(45, 1280.F / 769.F, 0.1F, 100.0F);
        controller.position.set(0, 0, 0);
        controller.UpdateVectors();

        MOD = new Model("src/main/resources/models/backpack.obj");

        float[] vertices = new float[]{
                0.0f, 0.5f, 0.0f, 1, 1, 0,
                -0.5f, -0.5f, 0.0f, 0, 1, 1,
                0.5f, -0.5f, 0.0f, 1, 0, 1
        };


        FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
        verticesBuffer.put(vertices).flip();
        vao = GL32.glGenVertexArrays();
        GL32.glBindVertexArray(vao);
        vbo = GL32.glGenBuffers();
        GL32.glBindBuffer(GL32.GL_ARRAY_BUFFER, vbo);
        GL32.glBufferData(GL32.GL_ARRAY_BUFFER, verticesBuffer, GL32.GL_STATIC_DRAW);
        MemoryUtil.memFree(verticesBuffer);
        GL32.glVertexAttribPointer(0, 3, GL32.GL_FLOAT, false, 6 * (Float.SIZE / 8), 0);
        GL32.glEnableVertexAttribArray(0);
        GL32.glVertexAttribPointer(1, 3, GL32.GL_FLOAT, false, 6 * (Float.SIZE / 8), 3 * (Float.SIZE / 8));
        GL32.glEnableVertexAttribArray(1);
        GL32.glBindVertexArray(0);

    }

    @Override
    public void render(float delta) {
        if (ImGui.isMouseDown(1))
            controller.UpdateController(delta);
        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, fb.getId());

        GL32.glClearColor(0.1F, 0.1F, 0.1F, 1.0F);
        GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT | GL32.GL_STENCIL_BUFFER_BIT);



        GL32.glUseProgram(shader.getId());
        shader.setUniformMat4("view", controller.getViewMatrix());
        shader.setUniformMat4("proj", controller.getProjectionMatrix());

        Vector3f pos  =  new Vector3f(0, 0, 0);
        Vector3f rot  = new Vector3f(0, 0, 0);
        Vector3f scale = new Vector3f(1, 1, 1);

        Matrix4f model = new Matrix4f().translate(pos).scale(scale);
        shader.setUniformMat4("model", model);

        MOD.draw(shader);
//

//        GL32.glPixelStorei(GL32.GL_PACK_ALIGNMENT, 1);
//        ByteBuffer data = BufferUtils.createByteBuffer(1280 * 796 * 3);
//        GL32.glReadPixels(0, 0, 1280, 769, GL32.GL_RGB, GL32.GL_UNSIGNED_BYTE, data);
//
//        STBImageWrite.nstbi_flip_vertically_on_write(1);
//        STBImageWrite.stbi_write_jpg("test.jpg", 1280, 769, 3, data, 1280 * 3);
        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, 0);


    }

    @Override
    public void process() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.begin("Viewport", ImGuiWindowFlags.NoScrollbar);

        ImGui.image(fb.getTexture(), ImGui.getWindowSizeX(), ImGui.getWindowSizeY(), 0, 1, 1, 0);
        ImGui.end();
        ImGui.popStyleVar();
    }

    @Override
    public void postProcess() {

    }
}
