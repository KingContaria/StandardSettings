package com.kingcontaria.standardsettings.mixins;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)

public interface MinecraftClientAccessor {
    @Accessor
    String getOpenProfilerSection();
    @Accessor
    void setOpenProfilerSection(String value);
}
