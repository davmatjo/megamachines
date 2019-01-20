
uniform sampler2D sampler;
//uniform vec3 spriteColour;

varying vec2 tex_coords;


void main() {
    gl_FragColor = texture2D(sampler, tex_coords);
}
