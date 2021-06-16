package gfx;

import lombok.Getter;
import org.lwjgl.opengl.GL32;

import static org.lwjgl.system.MemoryUtil.NULL;
import static utils.Utils.Assert;

public class FrameBuffer {
    @Getter
    private final int id;
    @Getter
    private final int texture;
    @Getter
    private final int rbo;

    public FrameBuffer(int width, int height) {
        id = GL32.glGenFramebuffers();
        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, id);

        texture = GL32.glGenTextures();
        GL32.glBindTexture(GL32.GL_TEXTURE_2D, texture);
        GL32.glTexImage2D(GL32.GL_TEXTURE_2D, 0, GL32.GL_RGB, width, height, 0, GL32.GL_RGB, GL32.GL_UNSIGNED_BYTE, NULL);

        GL32.glTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_MIN_FILTER, GL32.GL_LINEAR);
        GL32.glTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_MAG_FILTER, GL32.GL_LINEAR);

        GL32.glFramebufferTexture2D(GL32.GL_FRAMEBUFFER, GL32.GL_COLOR_ATTACHMENT0, GL32.GL_TEXTURE_2D, texture, 0);

        rbo = GL32.glGenRenderbuffers();
        GL32.glBindRenderbuffer(GL32.GL_RENDERBUFFER, rbo);
        GL32.glRenderbufferStorage(GL32.GL_RENDERBUFFER, GL32.GL_DEPTH24_STENCIL8, width, height);
        GL32.glFramebufferRenderbuffer(GL32.GL_FRAMEBUFFER, GL32.GL_DEPTH_STENCIL_ATTACHMENT, GL32.GL_RENDERBUFFER, rbo);

        Assert(GL32.glCheckFramebufferStatus(GL32.GL_FRAMEBUFFER) == GL32.GL_FRAMEBUFFER_COMPLETE, "Failed to create framebuffer!");
        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, 0);
    }

    public void dispose() {
        GL32.glDeleteFramebuffers(id);
        GL32.glDeleteTextures(texture);
    }
}
