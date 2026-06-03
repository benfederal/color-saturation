package me.colorsaturation.client;

import me.colorsaturation.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ColorSaturationClient implements ClientModInitializer {

    public static KeyMapping openConfigKey;

    @Override
    public void onInitializeClient() {
        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.color-saturation.open_config",
                GLFW.GLFW_KEY_UNKNOWN,
                "key.category.color-saturation"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openConfigKey.consumeClick()) {
                Minecraft.getInstance().setScreen(
                        AutoConfig.getConfigScreen(ModConfig.class, null).get()
                );
            }
        });

        RenderEventListener.register();

        me.colorsaturation.ColorSaturation.LOGGER.info("Color Saturation Client initialized!");
    }
}