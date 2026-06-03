package me.colorsaturation.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaturationEffect {

    private static final Logger LOGGER = LoggerFactory.getLogger("color-saturation");
    private static final ResourceLocation SHADER_LOCATION = ResourceLocation.fromNamespaceAndPath("color-saturation", "shaders/post/color_saturation.json");

    private static PostChain postChain = null;
    private static boolean failed = false;

    public static void render(float saturation, float brightness, float contrast, float hue) {
        if (saturation == 0.0f && brightness == 0.0f && contrast == 0.0f && hue == 0.0f) {
            dispose();
            return;
        }
        if (failed) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        try {
            if (postChain == null) {
                postChain = new PostChain(
                        mc.getTextureManager(),
                        mc.getResourceManager(),
                        mc.getMainRenderTarget(),
                        SHADER_LOCATION
                );
                postChain.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
            }

            var pass = postChain.passes.get(0);
            pass.getEffect().safeGetUniform("Saturation").set(saturation);
            pass.getEffect().safeGetUniform("Brightness").set(brightness);
            pass.getEffect().safeGetUniform("Contrast").set(contrast);
            pass.getEffect().safeGetUniform("Hue").set(hue);

            postChain.process(mc.getTimer().getGameTimeDeltaPartialTick(true));
            mc.getMainRenderTarget().bindWrite(false);

        } catch (Exception e) {
            LOGGER.error("Color Saturation shader failed: ", e);
            failed = true;
            dispose();
        }
    }

    public static void dispose() {
        if (postChain != null) {
            postChain.close();
            postChain = null;
        }
    }
}