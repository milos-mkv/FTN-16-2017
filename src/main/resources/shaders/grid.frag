#version 330 core

out vec4 outColor;
in vec3 farPoint, nearPoint;
in mat4 fragView;
in mat4 fragProj;
//
//vec4 grid(vec3 fragPos3D, float scale, bool drawAxis) {
//    vec2 coord = fragPos3D.xz * scale;
//    vec2 derivative = fwidth(coord);
//    vec2 grid = abs(fract(coord - 0.5) - 0.5) / derivative;
//    float line = min(grid.x, grid.y);
//    float minimumz = min(derivative.y, 1);
//    float minimumx = min(derivative.x, 1);
//    vec4 color = vec4(0.2, 0.2, 0.2, 1.0 - min(line, 1.0));
//    // z axis
//    if(fragPos3D.x > -0.1 * minimumx && fragPos3D.x < 0.1 * minimumx)
//    color.z = 1.0;
//    // x axis
//    if(fragPos3D.z > -0.1 * minimumz && fragPos3D.z < 0.1 * minimumz)
//    color.x = 1.0;
//    return color;
//}
//float computeDepth(vec3 pos) {
//    vec4 clip_space_pos = fragProj * fragView * vec4(pos.xyz, 1.0);
//    return (clip_space_pos.z / clip_space_pos.w);
//}

void main() {
//    float t = -nearPoint.y / (farPoint.y - nearPoint.y);
//    vec3 fragPos3D = nearPoint + t * (farPoint - nearPoint);
//    gl_FragDepth = computeDepth(fragPos3D);
//    outColor = grid(fragPos3D, 10, true) * float(t > 0);
//    float t = -nearPoint.y / (farPoint.y - nearPoint.y);
//    outColor = vec4(1.0, 0.0, 0.0, 1.0 * float(t > 0));
}