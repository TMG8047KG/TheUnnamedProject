import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;

public class Main {

    private static Shader shader;
    private static Camera camera;
    private static int vaoID;
    private static float lastFrameTime;

    private static void init() {
        Window.createWindow();
        camera = new Camera(new Vector3f(0, 0, 3), new Vector3f(0, 0, 0));
        shader = new Shader("vertex.glsl", "fragment.glsl");
        shader.bind();
        shader.createUniform("view");
        shader.createUniform("projection");
        shader.unbind();
        vaoID = createCube();
        glEnable(GL_DEPTH_TEST);
        lastFrameTime = (float) glfwGetTime();
        gameLoop();
        Window.close();
    }

    public static void gameLoop() {
        while (!glfwWindowShouldClose(Window.getWindow())) {
            float currentFrameTime = (float) glfwGetTime();
            float deltaTime = currentFrameTime - lastFrameTime;
            lastFrameTime = currentFrameTime;

            glClearColor(0.5f, 0.1f, 0.8f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Window.update();
            render(deltaTime);

            glfwSwapBuffers(Window.getWindow());
            glfwPollEvents();
        }
    }

    public static void render(float deltaTime) {
        shader.bind();

        Matrix4f viewMatrix = new Matrix4f().identity();
        viewMatrix.rotate((float) Math.toRadians(camera.getRotation().x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(camera.getRotation().y), new Vector3f(0, 1, 0))
                .rotate((float) Math.toRadians(camera.getRotation().z), new Vector3f(0, 0, 1))
                .translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);

        shader.setUniform("view", viewMatrix);
        shader.setUniform("projection", Window.getProjectionMatrix());

        glBindVertexArray(vaoID);
        glDrawArrays(GL_TRIANGLES, 0, 36);

        glBindVertexArray(0);
        shader.unbind();
    }

    private static int createCube() {
        float[] vertices = {
                // Front face
                -0.5f,  0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,
                // Back face
                -0.5f,  0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                -0.5f,  0.5f, -0.5f,
                // Left face
                -0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,
                // Right face
                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                // Top face
                -0.5f,  0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                -0.5f,  0.5f, -0.5f,
                -0.5f,  0.5f,  0.5f,
                // Bottom face
                -0.5f, -0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f,  0.5f
        };

        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        return vao;
    }

    public static void main(String[] args) {
        init();
    }
}
