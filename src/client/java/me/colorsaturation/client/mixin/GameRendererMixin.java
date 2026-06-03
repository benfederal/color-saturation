package me.colorsaturation.client.mixin;

import me.colorsaturation.ModConfig;
import me.colorsaturation.client.SaturationEffect;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderTail(CallbackInfo ci) {
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
    }
}