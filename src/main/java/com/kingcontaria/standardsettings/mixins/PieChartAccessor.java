package com.kingcontaria.standardsettings.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.util.WindowProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)

public interface PieChartAccessor {
    @Accessor("openProfilerSection")
    void setopenProfilerSection(String value);

    @Accessor
    WindowSettings getWindowSettings();

    @Accessor
    WindowProvider getWindowProvider();
}
