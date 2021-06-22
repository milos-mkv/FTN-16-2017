#version 330 core

out vec4 FragColor;
in vec2 TexCoords;
in vec3 normals;
in vec3 fragPos;

struct Material {
    vec3 ambient, diffuse, specular;
    float shininess;
};

struct DirectionalLight {
    vec3 direction, ambient, diffuse, specular;
};


uniform DirectionalLight dirLight;
uniform Material         material;

uniform vec3 viewPos;

uniform sampler2D diffuseTexture;
uniform sampler2D specularTexture;
uniform sampler2D normalsTexture;

uniform bool isDiffuseTextureSet;
uniform bool isSpecularTextureSet;
uniform bool isNormalsTextureSet;


vec4 CalculateDirectionalLight(vec3 objColor)
{
    // Ambient
    vec3 ambient = dirLight.ambient * objColor;

    // Diffuse
    vec3 norm = normalize(normals);
    vec3 lightDir = normalize(-dirLight.direction);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = dirLight.diffuse * diff * objColor;

    // Specular
    vec3 viewDir = normalize(viewPos - fragPos);
    vec3 reflectDir = reflect(lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
    vec3 specular = dirLight.specular * spec * objColor;

    return vec4(ambient + diffuse + specular, 1.0f);
}

void main() {
    vec3 objColor;
    if(isDiffuseTextureSet)
        objColor = texture(diffuseTexture, TexCoords).xyz * material.diffuse;
    else
        objColor = material.diffuse;
    FragColor = CalculateDirectionalLight(objColor);
}