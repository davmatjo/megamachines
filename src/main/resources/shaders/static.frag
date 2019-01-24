#version 120

uniform sampler2D sampler;
uniform vec4 colour;

varying vec2 texCoords;

void main() {
    gl_FragColor = colour * texture2D(sampler, texCoords);
}