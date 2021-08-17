package gfx;

import exceptions.OpenGLFramebufferException;
import lombok.Getter;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.glTexImage2DMultisample;

public class MSFrameBuffer {

    @Getter
    private final int id;

    @Getter
    private final int texture;

    @Getter
    private final int rbo;

    public MSFrameBuffer(int width, int height) {
        id = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, id);

        texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, texture);

        glTexImage2DMultisample(
                GL_TEXTURE_2D_MULTISAMPLE,
                4,
                GL_RGB,
                width,
                height,
                true
        );


        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, texture, 0);

        rbo = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rbo);
        glRenderbufferStorageMultisample(
                GL_RENDERBUFFER,
                4,
                GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new OpenGLFramebufferException("Failed to create framebuffer!");
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }


    public void dispose() {
        glDeleteFramebuffers(id);
        glDeleteRenderbuffers(rbo);
        glDeleteTextures(texture);
    }
}
