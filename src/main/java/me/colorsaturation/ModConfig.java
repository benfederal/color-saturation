package me.colorsaturation;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "color-saturation")
public class ModConfig implements ConfigData {

    public float hueValue = 0.0f;
    public float saturationValue = 0.0f;
    public float brightnessValue = 0.0f;
    public float contrastValue = 0.0f;
}