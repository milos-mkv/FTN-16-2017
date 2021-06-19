#version 330

out vec4 color;

in vec3 vertexPosition;
in vec3 near;
in vec3 far;

float checkerboard(vec2 R, float scale) {
    return float((int(floor(R.x / scale)) + int(floor(R.y / scale))) % 2);
}

void main() {
    float t = -near.y / (far.y-near.y);
    vec3 R = near + t * (far-near);
    float c =
    checkerboard(R.xz, 1) * 0.3 + checkerboard(R.xz, 10) * 0.2 + checkerboard(R.xz, 100) * 0.1 + 0.1;
    c = c * float(t > 0);

    float spotlight = min(1.0, 1.5 - 0.02*length(R.xz));
    color = vec4(vec3(c*spotlight), 1);
}