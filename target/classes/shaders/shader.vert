#version 330 core

layout (location = 0) in vec3 pos;
layout (location = 1) in vec3 normals;
layout (location = 2) in vec2 texCoords;

out vec3 col;
uniform mat4 model, view, proj;
out vec2 TexCoords;

void main() {
    gl_Position = proj * view * model * vec4(pos, 1);
    col = normals;
    TexCoords = vec2(texCoords.x, -texCoords.y);
}