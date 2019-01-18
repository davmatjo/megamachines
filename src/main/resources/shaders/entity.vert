#version 120

vec3 vertices;
vec2 texture;

uniform mat4 projection;
out vec2 tex_coords;

void main() {
    tex_coords = texture;
    gl_Position = projection * vec4(vertices, 1);
}