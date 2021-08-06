package gfx;

import exceptions.OpenGLFramebufferException;
import lombok.Getter;
import utils.Disposable;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class ShadowMap implements Disposable {

    private static ShadowMap shadowMap;

    public static ShadowMap getInstance() {
        return shadowMap == null ? shadowMap = new ShadowMap() : shadowMap;
    }

    @Getter
    private final int depthMapFBO;

    @Getter
    private final int depthMap;

    public static final int SHADOW_WIDTH = 4024;
    public static final int SHADOW_HEIGHT = 4024;

    private ShadowMap() {
        depthMapFBO = glGenFramebuffers();

        depthMap = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthMap);
        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_DEPTH_COMPONENT,
                SHADOW_WIDTH,
                SHADOW_HEIGHT,
                0,
                GL_DEPTH_COMPONENT,
                GL_FLOAT,
                (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
        glFramebufferTexture2D(
                GL_FRAMEBUFFER,
                GL_DEPTH_ATTACHMENT,
                GL_TEXTURE_2D,
                depthMap,
                0);
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new OpenGLFramebufferException("Could not create FrameBuffer");
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }


    @Override
    public void dispose() {
        glDeleteTextures(depthMap);
        glDeleteFramebuffers(depthMapFBO);
    }
}
