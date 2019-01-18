#version 120

uniform Sampler2D sampler;

in vec2 tex_coords;

void main() {
    gl_FragColor = texture2D(sampler, tex_coords);
}
