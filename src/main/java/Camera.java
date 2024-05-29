import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.opengl.GL46.*;

public class Camera {
    private Vector3f position, rotation;
    private float moveSpeed = 0.05f, mouseSensitivity = 0.15f;
    private double oldMouseX = 0, oldMouseY = 0, newMouseX, newMouseY;

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public void update() {
        float x = (float) Math.sin(Math.toRadians(rotation.x)) * moveSpeed;
        float z = (float) Math.cos(Math.toRadians(rotation.y)) * moveSpeed;

        new GLFWKeyCallback() {
            public void invoke ( long window, int key, int scancode, int action, int mods){
                if (action != GLFW.GLFW_RELEASE) {
                    System.out.println("key:" + key);
                    switch (key) {
                        case GLFW.GLFW_KEY_W -> position.add(new Vector3f(-x, 0, -z));
                        case GLFW.GLFW_KEY_A -> position.add(new Vector3f(-z, 0, x));
                        case GLFW.GLFW_KEY_S -> position.add(new Vector3f(x, 0, z));
                        case GLFW.GLFW_KEY_D -> position.add(new Vector3f(z, 0, -x));
                        case GLFW.GLFW_KEY_SPACE -> position.add(new Vector3f(0, moveSpeed, 0));
                        case GLFW.GLFW_KEY_LEFT_SHIFT -> position.add(new Vector3f(0, -moveSpeed, 0));
                    }
                }
            }
        };

        new GLFWCursorPosCallback() {
            public void invoke(long window, double xpos, double ypos) {
                System.out.println("x: " + xpos + " y: " + ypos);
                newMouseX = xpos;
                newMouseY = ypos;
            }
        };

        float dx = (float) (newMouseX - oldMouseX);
        float dy = (float) (newMouseY - oldMouseY);

        rotation.add(new Vector3f(-dy * mouseSensitivity, -dx * mouseSensitivity, 0));

        oldMouseX = newMouseX;
        oldMouseY = newMouseY;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }
}