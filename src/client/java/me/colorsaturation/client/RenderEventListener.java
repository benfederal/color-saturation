package me.colorsaturation.client;

import me.colorsaturation.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;

public class RenderEventListener {
    public static void register() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;
            if (mc.getOverlay() != null) return;

            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            SaturationEffect.render(
                    config.saturationValue,
                    config.brightnessValue,
                    config.contrastValue,
                    config.hueValue
            );
        });
    }
}