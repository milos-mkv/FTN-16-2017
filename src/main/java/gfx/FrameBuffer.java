package gfx;

import exceptions.OpenGLFramebufferException;
import lombok.Getter;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class FrameBuffer {

    @Getter
    private final int id;

    @Getter
    private final int texture;

    @Getter
    private final int rbo;

    public FrameBuffer(int width, int height, boolean ri) {
        id = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, id);

        texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);

        glTexImage2D(GL_TEXTURE_2D, 0, ri ? GL_R32I : GL_RGB, width, height, 0, ri ? GL_RED_INTEGER : GL_RGB,  GL_UNSIGNED_BYTE, NULL);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);

        rbo = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rbo);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new OpenGLFramebufferException("Failed to create framebuffer!");
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }


    public FrameBuffer(int width, int height) throws OpenGLFramebufferException {
        this(width, height, false);
    }

    public void dispose() {
        glDeleteFramebuffers(id);
        glDeleteRenderbuffers(rbo);
        glDeleteTextures(texture);
    }
}
