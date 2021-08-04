#version 330 core

layout (location = 0) in vec3 pos;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texcord;

uniform mat4 proj  = mat4(1);
uniform mat4 view  = mat4(1);
uniform mat4 model = mat4(1);

void main()
{
    gl_Position = proj * view * model * vec4(pos.x, pos.y, pos.z, 1.0);
}