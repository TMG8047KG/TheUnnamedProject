import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {
    private static int WIDTH = 800;
    private static int HEIGHT = 600;
    private final static String TITLE = "Game";
    private static long window;
    public static Camera camera = new Camera(new Vector3f(0, 0, 1), new Vector3f(100, 1, 100));

    static float rot = 0f;

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

//        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
//          WIDTH = width;
//          HEIGHT = height;
//          glViewport(0, 0, width, height);
//
//          glMatrixMode(GL_PROJECTION);
//          glLoadIdentity();
//          glOrtho(0, width, height, 0, 0f, 0f);
//          glMatrixMode(GL_MODELVIEW);
//          glLoadIdentity();
//        });

        glEnable(GL_DEPTH_TEST); // Enable depth testing

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        // Setup perspective projection
        gluPerspective(45.0f, (float)WIDTH / (float)HEIGHT, 0.1f, 100.0f);
        glMatrixMode(GL_MODELVIEW);


        glfwSetCursorPosCallback(window, (window, x, y) -> {
           System.out.println("x: " + x + " | y: " + y);
        });



        while (!glfwWindowShouldClose(window)){
            glClearColor(0.5f, 0.1f, 0.8f, 0.0f);
            render();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
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
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();
        // Move the camera back to see the cube
        glTranslatef(0.0f, 0.0f, -5.0f);

        glRotatef(rot, 1.0f, 1.0f, 1.0f); // Rotate the cube on X, Y & Z

        glBegin(GL_QUADS);
        glColor3f(0.0f, 1.0f, 0.0f); // Set The Color To Green
        glVertex3f(1.0f, 1.0f, -1.0f); // Top Right Of The Quad (Top)
        glVertex3f(-1.0f, 1.0f, -1.0f); // Top Left Of The Quad (Top)
        glVertex3f(-1.0f, 1.0f, 1.0f); // Bottom Left Of The Quad (Top)
        glVertex3f(1.0f, 1.0f, 1.0f);

        glColor3f(1.0f, 0.5f, 0.0f); // Set The Color To Orange
        glVertex3f(1.0f, -1.0f, 1.0f); // Top Right Of The Quad (Bottom)
        glVertex3f(-1.0f, -1.0f, 1.0f); // Top Left Of The Quad (Bottom)
        glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Left Of The Quad (Bottom)
        glVertex3f(1.0f, -1.0f, -1.0f); // Bottom Right Of The Quad (Bottom)

        glColor3f(1.0f, 0.0f, 0.0f); // Set The Color To Red
        glVertex3f(1.0f, 1.0f, 1.0f); // Top Right Of The Quad (Front)
        glVertex3f(-1.0f, 1.0f, 1.0f); // Top Left Of The Quad (Front)
        glVertex3f(-1.0f, -1.0f, 1.0f); // Bottom Left Of The Quad (Front)
        glVertex3f(1.0f, -1.0f, 1.0f);

        glColor3f(1.0f, 1.0f, 0.0f); // Set The Color To Yellow
        glVertex3f(1.0f, -1.0f, -1.0f); // Bottom Left Of The Quad (Back)
        glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Right Of The Quad (Back)
        glVertex3f(-1.0f, 1.0f, -1.0f); // Top Right Of The Quad (Back)
        glVertex3f(1.0f, 1.0f, -1.0f);

        glColor3f(0.0f, 0.0f, 1.0f); // Set The Color To Blue
        glVertex3f(-1.0f, 1.0f, 1.0f); // Top Right Of The Quad (Left)
        glVertex3f(-1.0f, 1.0f, -1.0f); // Top Left Of The Quad (Left)
        glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Left Of The Quad (Left)
        glVertex3f(-1.0f, -1.0f, 1.0f);

        glColor3f(1.0f, 0.0f, 1.0f); // Set The Color To Violet
        glVertex3f(1.0f, 1.0f, -1.0f); // Top Right Of The Quad (Right)
        glVertex3f(1.0f, 1.0f, 1.0f); // Top Left Of The Quad (Right)
        glVertex3f(1.0f, -1.0f, 1.0f); // Bottom Left Of The Quad (Right)
        glVertex3f(1.0f, -1.0f, -1.0f); // Bottom Right Of The Quad (Right)
        glEnd();

        rot += 0.2f;
    }


    public static void gluPerspective(float fovY, float aspect, float zNear, float zFar) {
        float fH = (float) Math.tan(Math.toRadians(fovY / 2)) * zNear;
        float fW = fH * aspect;
        glFrustum(-fW, fW, -fH, fH, zNear, zFar);
    }

    public static void main(String[] args) {
        createWindow();
        gameLoop();
        close();
    }
}
