package gfx;

import core.Constants;
import core.Window;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

public class FirstPersonCameraController extends PerspectiveCamera {

    public float yaw;
    public float pitch;
    public float speed;
    public float sensitivity;

    public FirstPersonCameraController(float fov, float aspect, float near, float far) {
        super(fov, aspect, near, far);
        this.yaw    = -90.0F;
        this.pitch  = 0.0F;
        this.speed  = 6.0F;
        this.sensitivity = 0.25F;
        UpdateCamera();
        UpdateVectors();
    }

    public void UpdateVectors() {
        front.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        front.y = (float) Math.sin(Math.toRadians(pitch));
        front.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));

        front.normalize();
        tmp.set(front).cross(Constants.WORLD_UP).normalize(right);
        tmp.set(right).cross(front).normalize(up);
    }

    @Override
    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(position, tmp.set(position).add(front), up);
    }

    public void UpdateController(float delta) {
        if(GLFW.glfwGetKey(Window.getHandle(), GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
            position.add(tmp.set(front).mul(speed * delta));
        }
        if(GLFW.glfwGetKey(Window.getHandle(), GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
            position.sub(tmp.set(front).mul(speed * delta));
        }
        if(GLFW.glfwGetKey(Window.getHandle(), GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
            position.sub(tmp.set(right).mul(speed * delta));
        }
        if(GLFW.glfwGetKey(Window.getHandle(), GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
            position.add(tmp.set(right).mul(speed * delta));
        }

        yaw += (Window.getMouse().x - Window.getMouse().z) * 0.25f;
        pitch += (Window.getMouse().w - Window.getMouse().y) * 0.25f;


        if (pitch > 89.0F)  pitch =  89.0F;
        if (pitch < -89.0F) pitch = -89.0F;

        UpdateVectors();
    }

}
