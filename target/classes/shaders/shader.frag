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
uniform vec3 lightPos;

uniform sampler2D diffuseTexture;
uniform sampler2D shadowMap;


uniform bool isDiffuseTextureSet;

in vec4 FragPosLightSpace;

float ShadowCalculation(vec4 fragPosLightSpace) {
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    projCoords = projCoords * 0.5 + 0.5;
    if(projCoords.z > 1.0f) {
        projCoords.z = 1.0f;
    }
    float closestDepth = texture(shadowMap, projCoords.xy).r;
    float bias = 0.05;
    return (closestDepth + bias) < projCoords.z ? 1.0 : 0.0;
}

vec4 CalculateDirectionalLight(vec3 objColor, float shadow)
{
    // Ambient
    vec3 ambient = dirLight.ambient * objColor;

    // Diffuse
    vec3 norm = normalize(normals);
    vec3 lightDir = normalize(-dirLight.direction);
    float dotLightNorm = dot(norm, lightDir);
    float diff = max(dotLightNorm, 0.0);
    vec3 diffuse = dirLight.diffuse * diff * objColor;

    // Specular
    vec3 viewDir = normalize(viewPos - fragPos);
    vec3 reflectDir = reflect(lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
    vec3 specular = dirLight.specular * spec * objColor;

//    return vec4(ambient + diffuse + specular, 1.0f);
    return vec4((ambient +(1.0 - shadow) * (diffuse + specular)) * objColor, 1.0f);
}

void main() {
    vec3 objColor;
    if(isDiffuseTextureSet)
        objColor = texture(diffuseTexture, TexCoords).xyz * material.diffuse;
    else
        objColor = material.diffuse;

    float shadow = ShadowCalculation(FragPosLightSpace);
    FragColor = CalculateDirectionalLight(objColor, shadow);
}