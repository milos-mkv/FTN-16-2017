#version 330 core

out vec4 FragColor;
in vec3 col;

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;

    float shininess;
};

uniform Material material;

uniform sampler2D diffuseTexture;
uniform sampler2D specularTexture;
uniform sampler2D normalsTexture;

in vec2 TexCoords;

void main() {
    FragColor = texture(diffuseTexture, TexCoords) * material.diffuse;
}