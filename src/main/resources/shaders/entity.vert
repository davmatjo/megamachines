#version 120

attribute vec3 vertices;
attribute vec2 textures;

varying vec2 texCoords;
//uniform mat4 model;
uniform mat4 projection;
uniform mat4 position;
uniform mat4 size;

void main() {
    texCoords = textures;
    gl_Position = projection * position * size * vec4(vertices, 1);
//    gl_Position = vec4(vertices, 1);
}