import core.Application;
import gfx.FirstPersonCameraController;
import gfx.FrameBuffer;
import gfx.Shader;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import lombok.SneakyThrows;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL32;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static utils.Utils.ReadFromFile;

public class Main extends Application {

    public static void main(String[] args) {
        launch(new Main());
    }

    private Shader shader;
    private FrameBuffer fb;
    private FirstPersonCameraController controller;

    int vao, vbo;

    @SneakyThrows
    @Override
    public void preProcess() {
        Vector3f a = new Vector3f(1, 1, 1);
        Vector3f b = (Vector3f) a.clone();
        b.x = 2;
        System.out.println(a.x);
        shader = new Shader(ReadFromFile("src/main/resources/shaders/shader.vert"), ReadFromFile("src/main/resources/shaders/shader.frag"));
        fb = new FrameBuffer(1280, 769);
        controller = new FirstPersonCameraController(67, 1280.F / 769.F, 0.1F, 100.0F);

        float[] vertices = new float[]{
                0.0f, 0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f
        };

        FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
        verticesBuffer.put(vertices).flip();
        vao = GL32.glGenVertexArrays();
        GL32.glBindVertexArray(vao);
        vbo = GL32.glGenBuffers();
        GL32.glBindBuffer(GL32.GL_ARRAY_BUFFER, vbo);
        GL32.glBufferData(GL32.GL_ARRAY_BUFFER, verticesBuffer, GL32.GL_STATIC_DRAW);
        MemoryUtil.memFree(verticesBuffer);
        GL32.glVertexAttribPointer(0, 3, GL32.GL_FLOAT, false, 0, 0);
        GL32.glEnableVertexAttribArray(0);
        GL32.glBindVertexArray(0);

    }

    @Override
    public void render(float delta) {
        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, fb.getId());

        GL32.glClearColor(0.1F, 0.1F, 0.1F, 1.0F);
        GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT | GL32.GL_STENCIL_BUFFER_BIT);

        GL32.glUseProgram(shader.getId());
        GL32.glBindVertexArray(vao);
        GL32.glDrawArrays(GL32.GL_TRIANGLES, 0, 3);

        GL32.glPixelStorei(GL32.GL_PACK_ALIGNMENT, 1);
        ByteBuffer data = BufferUtils.createByteBuffer(1280 * 796 * 3);
        GL32.glReadPixels(0, 0, 1280, 769, GL32.GL_RGB, GL32.GL_BYTE, data);

        STBImageWrite.stbi_write_jpg("test.jpg", 1280, 769, 3, data, 1280 * 3);
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
