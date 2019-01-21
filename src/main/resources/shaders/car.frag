
uniform sampler2D sampler;
uniform vec4 spriteColour;

varying vec2 texCoords;


void main() {
    gl_FragColor = texture2D(sampler, texCoords) * spriteColour;
//    gl_FragColor = vec4(0, 1, 0, 1);
}