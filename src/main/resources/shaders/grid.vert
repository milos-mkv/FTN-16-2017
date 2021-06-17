#version 330 core

layout (location = 0) in vec3 pos;

uniform mat4 model, view, proj;
out vec3 farPoint, nearPoint;
out mat4 fragView;
out mat4 fragProj;

vec3 UnprojectPoint(float x, float y, float z, mat4 v, mat4 projection) {
    mat4 viewInv = inverse(v);
    mat4 projInv = inverse(projection);
    vec4 unprojectedPoint = projInv *viewInv   * vec4(x, y, z, 1.0);
    return unprojectedPoint.xyz / unprojectedPoint.w;
}

void main() {
    nearPoint = UnprojectPoint(pos.x, pos.y, 0.0, view, proj).xyz;
    farPoint = UnprojectPoint(pos.x, pos.y, 1.0, view, proj).xyz;
    gl_Position = vec4(pos, 1.0);
    fragProj = proj;
    fragView = view;
}