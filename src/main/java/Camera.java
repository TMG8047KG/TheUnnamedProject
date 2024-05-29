import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    private static Vector3f position;
    private static Vector3f rotation;
    private static final float moveSpeed = 0.05f;
    private static final float mouseSensitivity = 0.1f;
    private static boolean mousePressed;
    private static double lastMouseX = -1;
    private static double lastMouseY = -1;

    public Camera(Vector3f position, Vector3f rotation) {
        Camera.position = position;
        Camera.rotation = rotation;
    }

    public static GLFWKeyCallback getKeyCallback() {
        return new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                    switch (key) {
                        case GLFW_KEY_W -> move(0, 0, -moveSpeed);
                        case GLFW_KEY_A -> move(-moveSpeed, 0, 0);
                        case GLFW_KEY_S -> move(0, 0, moveSpeed);
                        case GLFW_KEY_D -> move(moveSpeed, 0, 0);
                        case GLFW_KEY_SPACE -> move(0, moveSpeed, 0);
                        case GLFW_KEY_LEFT_SHIFT -> move(0, -moveSpeed, 0);
                    }
                }
            }
        };
    }

    public static GLFWMouseButtonCallback getMouseButtonCallback() {
        return new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                if (button == GLFW_MOUSE_BUTTON_LEFT) {
                    if (action == GLFW_PRESS) {
                        mousePressed = true;
                        lastMouseX = -1; // Reset to ensure we initialize correctly on press
                        lastMouseY = -1;
                    } else if (action == GLFW_RELEASE) {
                        mousePressed = false;
                    }
                }
            }
        };
    }

    public static GLFWCursorPosCallback getCursorCallback() {
        return new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                if (mousePressed) {
                    if (lastMouseX == -1) {
                        lastMouseX = xpos;
                        lastMouseY = ypos;
                    }

                    float xOffset = (float) (xpos - lastMouseX) * mouseSensitivity;
                    float yOffset = (float) (lastMouseY - ypos) * mouseSensitivity; // reversed since y-coordinates go from bottom to top

                    lastMouseX = xpos;
                    lastMouseY = ypos;

                    rotate(xOffset, yOffset, 0);
                }
            }
        };
    }

    public static void move(float offsetX, float offsetY, float offsetZ) {
        if (offsetZ != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            position.z += (float) Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }
        if (offsetX != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }
        position.y += offsetY;
    }

    public static void rotate(float offsetX, float offsetY, float offsetZ) {
        rotation.y += offsetX;
        rotation.x += offsetY;

        rotation.x = Math.max(-90, Math.min(90, rotation.x)); // constrain pitch
    }

    public void setRotation(float x, float y, float z) {
        rotation.set(x, y, z);
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }
}
