package com.battlezone.megamachines.renderer.game;

import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.util.AssetManager;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;

public class Shader {

    public static final Shader CAR = AssetManager.loadShader("/shaders/car");
    public static final Shader ENTITY = AssetManager.loadShader("/shaders/entity");
    public static final Shader STATIC = null;

    private final int programID;
    private final Map<String, Integer> uniforms = new HashMap<>();

    public Shader(String vertexShader, String fragmentShader) {

        // Compile vertex shader
        int vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
        compileShader(vertexShader, vertexShaderID);

        // Compile fragment shader
        int fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
        compileShader(fragmentShader, fragmentShaderID);

        // Create shader program
        programID = glCreateProgram();
        glAttachShader(programID, vertexShaderID);
        glAttachShader(programID, fragmentShaderID);

        glBindAttribLocation(programID, 0, "vertices");
        glBindAttribLocation(programID, 1, "textures");

        glLinkProgram(programID);
        if (glGetProgrami(programID, GL_LINK_STATUS) != 1) {
            throw new GLShaderException(glGetProgramInfoLog(programID));
        }

        glValidateProgram(programID);
        if (glGetProgrami(programID, GL_VALIDATE_STATUS) != 1) {
            throw new GLShaderException(glGetProgramInfoLog(programID));
        }

        glUseProgram(programID);
        int uniformCount = glGetProgrami(programID, GL_ACTIVE_UNIFORMS);
        for (int i=0; i<uniformCount; i++) {
            IntBuffer length = BufferUtils.createIntBuffer(10);
            IntBuffer size = BufferUtils.createIntBuffer(10);
            IntBuffer type = BufferUtils.createIntBuffer(10);
            ByteBuffer nameBuffer = BufferUtils.createByteBuffer(100);
            glGetActiveUniform(programID, i, length, size, type, nameBuffer);
            String name = StandardCharsets.US_ASCII.decode(nameBuffer).toString();
            int location = glGetUniformLocation(programID, name);
            System.out.println("name: " + name + " location: " + location);
            uniforms.put(name.trim(), location);
        }

        // Free up space as the program is linked
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);

    }


    private void compileShader(String shader, int shaderID) {
        glShaderSource(shaderID, shader);

        // Attempt to compile shader
        glCompileShader(shaderID);
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) != 1) {
            String err = glGetShaderInfoLog(shaderID);
            System.err.println(err);
            throw new GLShaderException(err);
        }
    }

    /**
     * Activates this shader for use
     */
    void use() {
        glUseProgram(programID);
    }

    /**
     * Set an openGL shader attribute
     *
     * @param name  name of attribute
     * @param value value as int
     */
    public void setInt(String name, int value) {
        glUniform1i(uniforms.get(name), value);
    }

    /**
     * Set an openGL shader attribute
     *
     * @param name  name of attribute
     * @param value value as 3D Vector
     */
    public void setVector3f(String name, Vector3f value) {
        glUniform4f(uniforms.get(name), value.x, value.y, value.z, 1f);
    }

    /**
     * Set an openGL shader attribute
     *
     * @param name  name of attribute
     * @param value value as 4D Vector
     */
    void setVector4f(String name, Vector4f value) {
        glUniform4f(uniforms.get(name), value.x, value.y, value.z, value.w);
    }

    /**
     * Set an openGL shader attribute
     *
     * @param name  name of attribute
     * @param value value as 4D Matrix
     */
    public void setMatrix4f(String name, Matrix4f value) {
        FloatBuffer matrixData = BufferUtils.createFloatBuffer(16);
        value.get(matrixData);
        glUniformMatrix4fv(uniforms.get(name), false, matrixData);
    }
}
