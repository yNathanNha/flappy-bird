#version 330 core

uniform sampler2D tex;
uniform float time;
layout (location = 0) out vec4 color;

void main() {
    color = vec4(1.0, 1.0, 1.0, max(0.01, 1.0 - time)); // Ensure time is always used
    if (time > 1.0) {
        discard;
    }
}
