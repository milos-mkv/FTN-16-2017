package core.callbacks;

import core.Window;
import org.lwjgl.glfw.GLFWCursorPosCallback;

public class CursorPosCallback extends GLFWCursorPosCallback {
    @Override
    public void invoke(long window, double xPos, double yPos) {
        Window.getMouse().x = (float) xPos;
        Window.getMouse().y = (float) yPos;
    }
}
