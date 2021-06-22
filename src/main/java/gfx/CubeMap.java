package gfx;

import lombok.Getter;
import org.lwjgl.opengl.GL32;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class CubeMap {

    @Getter
    private final int id;

    public CubeMap(ArrayList<String> faces) { // RIGHT => LEFT => TOP => BOTTOM => BACK => FRONT
        id = GL32.glGenTextures();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GL32.glBindTexture(GL32.GL_TEXTURE_CUBE_MAP, id);
            for (int i = 0; i < faces.size(); i++) {
                IntBuffer width  = stack.mallocInt(1);
                IntBuffer height = stack.mallocInt(1);
                IntBuffer noc    = stack.mallocInt(1);
                ByteBuffer image = STBImage.stbi_load(faces.get(i), width, height, noc, 0);
                GL32.glTexImage2D(GL32.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL32.GL_RGB, width.get(), height.get(), 0, GL32.GL_RGB, GL32.GL_UNSIGNED_BYTE, image);
            }

            GL32.glTexParameteri(GL32.GL_TEXTURE_CUBE_MAP, GL32.GL_TEXTURE_MAG_FILTER, GL32.GL_LINEAR);
            GL32.glTexParameteri(GL32.GL_TEXTURE_CUBE_MAP, GL32.GL_TEXTURE_MIN_FILTER, GL32.GL_LINEAR);
            GL32.glTexParameteri(GL32.GL_TEXTURE_CUBE_MAP, GL32.GL_TEXTURE_WRAP_S, GL32.GL_CLAMP_TO_EDGE);
            GL32.glTexParameteri(GL32.GL_TEXTURE_CUBE_MAP, GL32.GL_TEXTURE_WRAP_T, GL32.GL_CLAMP_TO_EDGE);
            GL32.glTexParameteri(GL32.GL_TEXTURE_CUBE_MAP, GL32.GL_TEXTURE_WRAP_R, GL32.GL_CLAMP_TO_EDGE);
            GL32.glBindTexture(GL32.GL_TEXTURE_CUBE_MAP, 0);
        }
    }

    public void dispose() {
        GL32.glDeleteTextures(id);
    }
}
