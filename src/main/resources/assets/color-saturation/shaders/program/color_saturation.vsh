#version 150

in vec3 Position;
in vec2 UV0;

uniform mat4 ProjMat;
uniform mat4 InvProjMat;

out vec2 texCoord0;
out vec4 gl_Position;

void main() {
    gl_Position = vec4(Position, 1.0);
    texCoord0 = UV0;
}