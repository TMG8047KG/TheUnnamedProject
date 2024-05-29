import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL46.*;

public class Shader {

    private final int programId;
    private final int vertexShaderId;
    private final int fragmentShaderId;
    private final Map<String, Integer> uniforms;

    public Shader(String vertexShaderFile, String fragmentShaderFile) {
        programId = glCreateProgram();
        uniforms = new HashMap<>();

        vertexShaderId = loadShader("shaders/" + vertexShaderFile, GL_VERTEX_SHADER);
        fragmentShaderId = loadShader("shaders/" + fragmentShaderFile, GL_FRAGMENT_SHADER);

        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);

        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println(glGetProgramInfoLog(programId));
            System.exit(-1);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == GL_FALSE) {
            System.err.println(glGetProgramInfoLog(programId));
            System.exit(-1);
        }
    }

    private int loadShader(String file, int type) {
        int shaderId = glCreateShader(type);
        try {
            String shaderSource = new String(Files.readAllBytes(Paths.get(file)));
            glShaderSource(shaderId, shaderSource);
            glCompileShader(shaderId);
            if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
                System.err.println(glGetShaderInfoLog(shaderId));
                System.exit(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return shaderId;
    }

    public void createUniform(String uniformName) {
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0) {
            throw new RuntimeException("Could not find uniform: " + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }

    public void setUniform(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
        }
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDetachShader(programId, vertexShaderId);
            glDetachShader(programId, fragmentShaderId);
        }
        if (vertexShaderId != 0) {
            glDeleteShader(vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDeleteShader(fragmentShaderId);
        }
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
}
