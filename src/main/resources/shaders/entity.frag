
uniform sampler2D sampler;
//uniform vec3 spriteColour;

varying vec2 texCoords;


void main() {
    gl_FragColor = texture2D(sampler, texCoords);
//    gl_FragColor = vec4(0, 1, 0, 1);
}
