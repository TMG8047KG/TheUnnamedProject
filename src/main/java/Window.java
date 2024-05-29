import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private static int WIDTH = 800;
    private static int HEIGHT = 600;
    private static final String TITLE = "Game";
    private static long window;

    public static Matrix4f projectionMatrix;
    private static boolean isResized;
    private static final boolean isFullscreen = false;

    private static long time;
    private static int frames;
    private static int fps;

    public static void createWindow() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        window = glfwCreateWindow(WIDTH, HEIGHT, TITLE, NULL, NULL);
        if (window == NULL) throw new RuntimeException("Failed to create the GLFW window");

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            if (vidmode != null) {
                glfwSetWindowPos(window,
                        (vidmode.width() - pWidth.get(0)) / 2,
                        (vidmode.height() - pHeight.get(0)) / 2);
            }
        }

        glfwMakeContextCurrent(window);
        glfwShowWindow(window);
        GL.createCapabilities();
        glfwSwapInterval(1);
        createCallbacks();
        createProjectionMatrix();
    }

    public static void update() {
        if (isResized) {
            glViewport(0, 0, WIDTH, HEIGHT);
            isResized = false;
            updateProjectionMatrix();
        }
        glfwPollEvents();
        frames++;
        if (System.currentTimeMillis() > time + 1000) {
            fps = frames;
            time = System.currentTimeMillis();
            frames = 0;
        }
        glfwSetWindowTitle(window, TITLE + " | FPS:" + fps);
    }

    private static void createCallbacks() {
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            WIDTH = width;
            HEIGHT = height;
            isResized = true;
        });
        glfwSetKeyCallback(window, Camera.getKeyCallback());
        glfwSetMouseButtonCallback(window, Camera.getMouseButtonCallback());
        glfwSetCursorPosCallback(window, Camera.getCursorCallback());
    }

    private static void createProjectionMatrix() {
        float aspectRatio = (float) WIDTH / (float) HEIGHT;
        float y_scale = (float) (1f / Math.tan(Math.toRadians(45.0 / 2.0))); // Changed FOV from 90 to 45 for more natural perspective
        float x_scale = y_scale / aspectRatio;
        float frustum_length = 1000f - 0.1f; // Adjusted near clipping plane to 0.1f

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00(x_scale);
        projectionMatrix.m11(y_scale);
        projectionMatrix.m22(-((1000f + 0.1f) / frustum_length));
        projectionMatrix.m23(-1);
        projectionMatrix.m32(-((2 * 0.1f * 1000f) / frustum_length));
        projectionMatrix.m33(0);
    }

    private static void updateProjectionMatrix() {
        float aspectRatio = (float) WIDTH / (float) HEIGHT;
        float y_scale = (float) (1f / Math.tan(Math.toRadians(45.0 / 2.0)));
        float x_scale = y_scale / aspectRatio;
        projectionMatrix.m00(x_scale);
        projectionMatrix.m11(y_scale);
    }

    public static void close() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public static int getFps() {
        return fps;
    }

    public static long getWindow() {
        return window;
    }

    public static Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }
}
