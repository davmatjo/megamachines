package com.battlezone.megamachines.renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL46.*;

public class Shader {

    final int vertexShaderID;
    final int fragmentShaderID;
    final int programID;

    public Shader(String vertexShader, String fragmentShader) {

        // Compile vertex shader
        vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
        compileShader(vertexShader, GL_VERTEX_SHADER);

        // Compile fragment shader
        fragmentShaderID = glCreateShader(GL_VERTEX_SHADER);
        compileShader(fragmentShader, GL_FRAGMENT_SHADER);

        // Create shader program
        programID = glCreateProgram();
        glAttachShader(programID, vertexShaderID);
        glAttachShader(programID, fragmentShaderID);

        glBindAttribLocation(programID, 0, "vertices");
        glBindAttribLocation(programID, 1, "texture");

        glLinkProgram(programID);
        if (glGetProgrami(programID, GL_LINK_STATUS) != 1) {
            throw new GLShaderException(glGetProgramInfoLog(programID));
        }

        glValidateProgram(programID);
        if (glGetProgrami(programID, GL_VALIDATE_STATUS) != 1) {
            throw new GLShaderException(glGetProgramInfoLog(programID));
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
            throw new GLShaderException(glGetShaderInfoLog(shaderID));
        }
    }

    private void use() {
        glUseProgram(programID);
    }

    public void setFloat(String name, int value) {
        int location = glGetUniformLocation(programID, name);
        if (location != -1) {
            glUniform1i(location, value);
        } else {
            throw new GLShaderException("Could not find shader attribute");
        }
    }

    public void setVector3f(String name, Vector3f value) {
        int location = glGetUniformLocation(programID, name);
        if (location != -1) {
            glUniform4f(location, value.x, value.y, value.z, 1f);
        } else {
            throw new GLShaderException("Could not find shader attribute");
        }
    }

    public void setVector4f(String name, Vector4f value) {
        int location = glGetUniformLocation(programID, name);
        if (location != -1) {
            glUniform4f(location, value.x, value.y, value.z, value.w);
        } else {
            throw new GLShaderException("Could not find shader attribute");
        }
    }

    public void setMatrix4f(String name, Matrix4f value) {
        int location = glGetUniformLocation(programID, name);
        FloatBuffer matrixData = BufferUtils.createFloatBuffer(16);
        value.get(matrixData);
        if (location != -1) {
            glUniformMatrix4fv(location, false, matrixData);
        } else {
            throw new GLShaderException("Could not find shader attribute");
        }
    }
}
