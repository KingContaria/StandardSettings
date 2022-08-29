package com.kingcontaria.standardsettings.mixins.accessors;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)

public interface MinecraftClientAccessor {
    @Accessor
    void setOpenProfilerSection(String value);
    @Accessor
    String getOpenProfilerSection();
}
