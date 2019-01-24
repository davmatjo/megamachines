#version 120

attribute vec3 vertices;
attribute vec2 textures;

varying vec2 texCoords;

uniform mat4 position;
uniform mat4 texturePosition;

void main() {
    texCoords = (texturePosition * vec4(textures, 0, 1)).xy;
    gl_Position = position * vec4(vertices, 1);
}