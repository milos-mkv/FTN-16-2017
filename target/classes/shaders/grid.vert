#version 330

layout (location = 0) in vec3 P;    // position attr from the vbo

uniform mat4 proj;      // projection matrix
uniform mat4 view;       // modelview matrix

out vec3 vertexPosition;       // vertex position for the fragment shader
out vec3 near;
out vec3 far;

vec3 UnprojectPoint(float x, float y, float z, mat4 view, mat4 projection) {
    mat4 viewInv = inverse(view);
    mat4 projInv = inverse(projection);
    vec4 unprojectedPoint =  viewInv * projInv * vec4(x, y, z, 1.0);
    return unprojectedPoint.xyz / unprojectedPoint.w;
}

void main() {
    vertexPosition = P.xyz;
    near = UnprojectPoint(P.x, P.y, 0.0, view, proj).xyz;
    far = UnprojectPoint(P.x, P.y, 1.0, view, proj).xyz;
    gl_Position = vec4(P, 1);
}