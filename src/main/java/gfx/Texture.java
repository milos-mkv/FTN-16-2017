package gfx;

import lombok.Getter;
import org.lwjgl.opengl.GL32;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Texture {

    @Getter
    private final int id;

    public Texture(final String filePath) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width  = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer noc    = stack.mallocInt(1);

            ByteBuffer data = STBImage.stbi_load(filePath, width, height, noc, 0);

            if (data == null) {
                throw new RuntimeException("Failed to load image: " + filePath);
            }

            int format = (noc.get(0) == 3) ? GL32.GL_RGB : (noc.get(0) == 4) ? GL32.GL_RGBA : GL32.GL_RED;

            id = GL32.glGenTextures();
            GL32.glBindTexture(GL32.GL_TEXTURE_2D, id);
            GL32.glTexImage2D(GL32.GL_TEXTURE_2D, 0, format, width.get(0), height.get(0), 0, format, GL32.GL_UNSIGNED_BYTE, data);
            GL32.glGenerateMipmap(GL32.GL_TEXTURE_2D);
            GL32.glTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_WRAP_S, GL32.GL_REPEAT);
            GL32.glTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_WRAP_T, GL32.GL_REPEAT);
            GL32.glTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_MIN_FILTER, GL32.GL_LINEAR);
            GL32.glTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_MAG_FILTER, GL32.GL_LINEAR);

            STBImage.stbi_image_free(data);
        }
    }

    public void dispose() {
        GL32.glDeleteTextures(id);
    }

}
