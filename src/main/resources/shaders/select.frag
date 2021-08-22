#version 330 core

out int outColor;

uniform int id;

void main()
{
    outColor = id;
}