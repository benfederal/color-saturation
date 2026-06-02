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


    @Inject(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;renderLevel(Lnet/minecraft/client/DeltaTracker;)V", shift = At.Shift.AFTER)
    )
    private void onAfterRenderLevel(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        if (mc.getOverlay() != null) return;

        // F1 protection
        if (mc.options.hideGui) {
            return;
        }

        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        SaturationEffect.render(
                config.saturationValue,
                config.brightnessValue,
                config.contrastValue,
                config.hueValue
        );


        com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}