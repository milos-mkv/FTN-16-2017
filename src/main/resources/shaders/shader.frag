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

uniform sampler2D shadowMap;
uniform sampler2D diffuseTexture;
uniform sampler2D specularTexture;
uniform sampler2D normalTexture;

uniform bool isDiffuseTextureSet;
uniform bool isSpecularTextureSet;
uniform bool isNormalTextureSet;

uniform bool isUsingDirectionalLight;
uniform bool isUsingShadows;

in vec4 FragPosLightSpace;

float ShadowCalculation(vec4 fragPosLightSpace) {
    // perform perspective divide
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    // transform to [0,1] range
    projCoords = projCoords * 0.5 + 0.5;
    // get closest depth value from light's perspective (using [0,1] range fragPosLight as coords)
    float closestDepth = texture(shadowMap, projCoords.xy).r;
    // get depth of current fragment from light's perspective
    float currentDepth = projCoords.z;
    // calculate bias (based on depth map resolution and slope)
    vec3 normal = normalize(normals);
    vec3 lightDir = normalize(lightPos - fragPos);
    float bias = max(0.05 * (1.0 - dot(normal, lightDir)), 0.005);
    // check whether current frag pos is in shadow
    // float shadow = currentDepth - bias > closestDepth  ? 1.0 : 0.0;
    // PCF
    float shadow = 0.0;
    vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
    for (int x = -1; x <= 1; ++x) {
        for (int y = -1; y <= 1; ++y) {
            float pcfDepth = texture(shadowMap, projCoords.xy + vec2(x, y) * texelSize).r;
            shadow += currentDepth - bias > pcfDepth  ? 1.0 : 0.0;
        }
    }
    shadow /= 9.0;

    // keep the shadow at 0.0 when outside the far_plane region of the light's frustum.
    if (projCoords.z > 1.0) {
        shadow = 0.0;
    }
    return shadow;
}

vec4 CalculateDirectionalLight(vec3 color, vec3 specularColor, float shadow) {
    // Ambient
    vec3 ambient = dirLight.ambient * color * material.ambient;

    // Diffuse
    vec3 norm = normalize(normals);
    vec3 lightDir = normalize(-dirLight.direction);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = dirLight.diffuse * diff * color;

    // Specular
    vec3 viewDir = normalize(viewPos - fragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = dirLight.specular * spec * specularColor;//* color;

    return vec4((ambient +(1.0 - shadow) * (diffuse + specular)) * color, 1.0f);
}

vec4 CalculateDirectionalLight(vec3 color, vec3 specularColor) {
    // Ambient
    vec3 ambient = dirLight.ambient * color * material.ambient;

    // Diffuse
    vec3 norm = normalize(normals);
    vec3 lightDir = normalize(-dirLight.direction);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = dirLight.diffuse * diff * color;

    // Specular
    vec3 viewDir = normalize(viewPos - fragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = dirLight.specular * spec * specularColor;

    return vec4(ambient + diffuse + specular, 1.0f);
}

void main() {
    vec3 objColor;
    vec3 specularColor;


    if (isDiffuseTextureSet) {
        objColor = texture(diffuseTexture, TexCoords).xyz * material.diffuse;
    }
    else {
        objColor = material.diffuse;
    }

    if (isSpecularTextureSet) {
        specularColor = texture(specularTexture,  TexCoords).xyz * material.specular;
    }
    else {
        specularColor = material.specular;
    }

    float shadow = ShadowCalculation(FragPosLightSpace);

    if (isUsingDirectionalLight) {
        if(isUsingShadows) {
            FragColor = CalculateDirectionalLight(objColor, specularColor, shadow);
        } else {
            FragColor = CalculateDirectionalLight(objColor, specularColor);
        }
    } else {
        FragColor = vec4(objColor, 1.0f);
    }
}