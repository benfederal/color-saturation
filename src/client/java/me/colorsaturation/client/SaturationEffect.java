package me.colorsaturation.client;

import net.minecraft.client.Minecraft;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL32;
import java.nio.FloatBuffer;

public class SaturationEffect {

    private static int shaderProgram = -1;
    private static int vao = -1;
    private static int colorTexture = -1;
    private static int prevWidth = -1;
    private static int prevHeight = -1;

    private static final String VERT = """
        #version 150
        in vec2 Position;
        out vec2 texCoord;
        void main() {
            texCoord = Position * 0.5 + 0.5;
            gl_Position = vec4(Position, 0.0, 1.0);
        }
        """;

    private static final String FRAG = """
        #version 150
        uniform sampler2D Sampler0;
        uniform float Saturation;
        uniform float Brightness;
        uniform float Contrast;
        uniform float Hue;
        in vec2 texCoord;
        out vec4 fragColor;
        
        vec3 rgb2hsv(vec3 c) {
            vec4 K = vec4(0.0, -1.0/3.0, 2.0/3.0, -1.0);
            vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
            vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));
            float d = q.x - min(q.w, q.y);
            float e = 1.0e-10;
            return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
        }
        
        vec3 hsv2rgb(vec3 c) {
            vec4 K = vec4(1.0, 2.0/3.0, 1.0/3.0, 3.0);
            vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
            return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
        }
        
        void main() {
            vec4 color = texture(Sampler0, texCoord);
            
            float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
            color.rgb = mix(vec3(gray), color.rgb, 1.0 + Saturation);
            
            vec3 hsv = rgb2hsv(color.rgb);
            hsv.x = fract(hsv.x + Hue);
            color.rgb = hsv2rgb(hsv);
            
            color.rgb += Brightness;
            
            color.rgb = (color.rgb - 0.5) * (1.0 + Contrast) + 0.5;
            
            fragColor = vec4(clamp(color.rgb, 0.0, 1.0), color.a);
        }
        """;

    public static void render(float saturation, float brightness, float contrast, float hue) {
        if (saturation == 0.0f && brightness == 0.0f && contrast == 0.0f && hue == 0.0f) return;
        if (shaderProgram == -1) init();

        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getWidth();
        int height = mc.getWindow().getHeight();

        if (width != prevWidth || height != prevHeight) {
            prevWidth = width;
            prevHeight = height;
        }

        GL32.glBindTexture(GL32.GL_TEXTURE_2D, colorTexture);
        GL32.glCopyTexImage2D(GL32.GL_TEXTURE_2D, 0, GL32.GL_RGB, 0, 0, width, height, 0);

        GL32.glUseProgram(shaderProgram);
        GL32.glUniform1i(GL32.glGetUniformLocation(shaderProgram, "Sampler0"), 0);
        GL32.glUniform1f(GL32.glGetUniformLocation(shaderProgram, "Saturation"), saturation);
        GL32.glUniform1f(GL32.glGetUniformLocation(shaderProgram, "Brightness"), brightness);
        GL32.glUniform1f(GL32.glGetUniformLocation(shaderProgram, "Contrast"), contrast);
        GL32.glUniform1f(GL32.glGetUniformLocation(shaderProgram, "Hue"), hue);

        GL32.glBindVertexArray(vao);
        GL32.glDrawArrays(GL32.GL_TRIANGLE_STRIP, 0, 4);
        GL32.glBindVertexArray(0);
        GL32.glUseProgram(0);
    }

    private static void init() {
        int vert = GL32.glCreateShader(GL32.GL_VERTEX_SHADER);
        GL32.glShaderSource(vert, VERT);
        GL32.glCompileShader(vert);

        int frag = GL32.glCreateShader(GL32.GL_FRAGMENT_SHADER);
        GL32.glShaderSource(frag, FRAG);
        GL32.glCompileShader(frag);

        shaderProgram = GL32.glCreateProgram();
        GL32.glAttachShader(shaderProgram, vert);
        GL32.glAttachShader(shaderProgram, frag);
        GL32.glLinkProgram(shaderProgram);
        GL32.glDeleteShader(vert);
        GL32.glDeleteShader(frag);

        vao = GL32.glGenVertexArrays();
        int vbo = GL32.glGenBuffers();
        GL32.glBindVertexArray(vao);
        GL32.glBindBuffer(GL32.GL_ARRAY_BUFFER, vbo);

        FloatBuffer verts = BufferUtils.createFloatBuffer(8);
        verts.put(new float[]{-1,-1, 1,-1, -1,1, 1,1}).flip();
        GL32.glBufferData(GL32.GL_ARRAY_BUFFER, verts, GL32.GL_STATIC_DRAW);
        GL32.glEnableVertexAttribArray(0);
        GL32.glVertexAttribPointer(0, 2, GL32.GL_FLOAT, false, 0, 0);
        GL32.glBindVertexArray(0);

        colorTexture = GL32.glGenTextures();
        GL32.glBindTexture(GL32.GL_TEXTURE_2D, colorTexture);
        GL32.glTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_MIN_FILTER, GL32.GL_LINEAR);
        GL32.glTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_MAG_FILTER, GL32.GL_LINEAR);
    }
}