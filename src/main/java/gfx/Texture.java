package gfx;

import utils.Disposable;
import exceptions.InvalidDocumentException;
import lombok.Getter;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Texture implements Disposable {

    @Getter
    private final int id;

    @Getter
    private final String path;

    @Getter
    private final int width;

    @Getter
    private final int height;

    public Texture(final String filePath) throws InvalidDocumentException {
        path = filePath;
        try (var stack = MemoryStack.stackPush()) {
            IntBuffer width  = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer noc    = stack.mallocInt(1);

            ByteBuffer data = STBImage.stbi_load(filePath, width, height, noc, 0);

            if (data == null) {
                throw new InvalidDocumentException("Failed to load image: " + filePath);
            }

            this.width  = width.get();
            this.height = height.get();

            int format;
            switch (noc.get(0)) {
                case 3:  format = GL_RGB;  break;
                case 4:  format = GL_RGBA; break;
                default: format = GL_RED;
            }

            id = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, id);
            glTexImage2D(GL_TEXTURE_2D, 0, format, width.get(0), height.get(0), 0, format, GL_UNSIGNED_BYTE, data);
            glGenerateMipmap(GL_TEXTURE_2D);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            STBImage.stbi_image_free(data);
        }
    }

    @Override
    public void dispose() {
        glDeleteTextures(id);
    }

}
