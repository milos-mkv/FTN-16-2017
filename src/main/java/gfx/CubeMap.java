package gfx;

import exceptions.InvalidDocumentException;
import lombok.Getter;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL13.*;

public class CubeMap {

    @Getter
    private final int id;

    @Getter
    private List<String> faces;

    // RIGHT => LEFT => TOP => BOTTOM => BACK => FRONT
    public CubeMap(List<String> faces) throws InvalidDocumentException {
        this.faces = faces;
        id = glGenTextures();

        try (var stack = MemoryStack.stackPush()) {
            glBindTexture(GL_TEXTURE_CUBE_MAP, id);

            for (var i = 0; i < faces.size(); i++) {
                IntBuffer width = stack.mallocInt(1);
                IntBuffer height = stack.mallocInt(1);
                IntBuffer noc = stack.mallocInt(1);
                ByteBuffer image = STBImage.stbi_load(faces.get(i), width, height, noc, 0);

                if (image == null) {
                    System.out.println("ERR");
                    throw new InvalidDocumentException("Failed to load image for CubeMap: " + faces.get(i));
                }

                glTexImage2D(
                        GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
                        0,
                        GL_RGB,
                        width.get(),
                        height.get(),
                        0,
                        GL_RGB,
                        GL_UNSIGNED_BYTE,
                        image);
            }

            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
            glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
        }
    }

    public void dispose() {
        glDeleteTextures(id);
    }
}
