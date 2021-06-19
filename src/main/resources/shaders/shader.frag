#version 330 core

out vec4 FragColor;
in vec3 col;

uniform sampler2D tex;
uniform sampler2D tex1;
in vec2 TexCoords;
void main() {
    FragColor = texture(tex, TexCoords) ;//* texture(tex1, TexCoords); //l* vec4(col, 1);
}