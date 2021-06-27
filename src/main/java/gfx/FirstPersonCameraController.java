package gfx;

import core.Constants;
import core.Window;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

@EqualsAndHashCode(callSuper = true)
@Data
public class FirstPersonCameraController extends PerspectiveCamera {

    protected float yaw;
    protected float pitch;
    protected float speed;
    protected float sensitivity;

    public FirstPersonCameraController(float fov, float aspect, float near, float far) {
        super(fov, aspect, near, far);
        this.yaw    = -90.0F;
        this.pitch  = 0.0F;
        this.speed  = 6.0F;
        this.sensitivity = 0.25F;
        updateCamera();
        updateVectors();
    }

    public void updateVectors() {
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

    public void updateController(float delta) {
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

        updateVectors();
    }

}
