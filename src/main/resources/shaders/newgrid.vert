#version 330 core

layout (location = 0) in vec3 pos;

uniform mat4 view, proj;

out vec3 nearPoint;
out vec3 farPoint;
out mat4 fragView;
out mat4 fragProj;
vec3 UnprojectPoint(float x, float y, float z, mat4 view, mat4 projection) {
    mat4 viewInv = inverse(view);
    mat4 projInv = inverse(projection);
    vec4 unprojectedPoint =  viewInv * projInv * vec4(x, y, z, 1.0);
    return unprojectedPoint.xyz / unprojectedPoint.w;
}

void main() {
    vec3 p = pos;
    nearPoint = UnprojectPoint(p.x, p.y, 0.0, view, proj).xyz; // unprojecting on the near plane
    farPoint = UnprojectPoint(p.x, p.y, 1.0, view, proj).xyz; // unprojecting on the far plane
    gl_Position = vec4(p, 1.0);
    fragView = view;
    fragProj = proj;
}