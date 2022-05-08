package com.kingcontaria.standardsettings.mixins;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)

public interface PieChartAccessor {
    @Accessor("openProfilerSection")
    String getopenProfilerSection();

    @Accessor("openProfilerSection")
    void setopenProfilerSection(String value);
}
