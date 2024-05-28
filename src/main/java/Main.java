import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {
    private final static int WIDTH = 800;
    private final static int HEIGHT = 600;
    private final static String TITLE = "Game";
    private static long window;
    public static Camera camera = new Camera(new Vector3f(0, 0, 1), new Vector3f(0, 0, 0));

    public static void createWindow(){
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

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
    }

    public static void gameLoop(){
        GL.createCapabilities();

        FloatBuffer ambient = BufferUtils.createFloatBuffer(4);
        ambient.put(new float[] { 0.05f, 0.05f, 0.05f, 1f, });
        ambient.flip();

        FloatBuffer position = BufferUtils.createFloatBuffer(4);
        position.put(new float[] { 0f, 0f, 0f, 1f, });
        position.flip();

        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glLightModelfv(GL_LIGHT_MODEL_AMBIENT, ambient);
        glLightfv(GL_LIGHT0, GL_POSITION, position);

        while (!glfwWindowShouldClose(window)){
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            camera.update();
            render();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    public static void close() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }


    public static void render(){
        glClearColor(0.5f, 0.0f, 0.8f, 0.0f);

        glBegin(GL_QUADS);
        glNormal3f(0.0f, 1.0f, 0.0f); // Normal pointing upwards
        glColor3f(1.0f, 1.0f, 1.0f); // Set color to white to see lighting effects
        glVertex3f(-50, 0, -50);
        glVertex3f(-50, 0, 50);
        glVertex3f(50, 0, 50);
        glVertex3f(50, 0, -50);
        glEnd();
    }

    public static void main(String[] args) {
        createWindow();
        gameLoop();
        close();
    }
}
