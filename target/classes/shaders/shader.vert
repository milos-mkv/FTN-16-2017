#version 330 core

layout (location = 0) in vec3 pos;
layout (location = 1) in vec3 Normals;
layout (location = 2) in vec2 texCoords;

uniform mat4 model, view, proj;
out vec2 TexCoords;
out vec3 normals;
out vec3 fragPos;

void main() {
    gl_Position = proj * view * model * vec4(pos, 1);
    TexCoords = vec2(texCoords.x, -texCoords.y);
    normals = mat3(transpose(inverse(model))) * Normals;
    fragPos =  vec3(model * vec4(pos, 1.0f));
}