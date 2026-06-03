#version 150

in vec2 texCoord0;

uniform sampler2D DiffuseSampler;
uniform float Saturation;

out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord0);

    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
    vec3 result = mix(vec3(gray), color.rgb, Saturation);

    fragColor = vec4(result, color.a);
}