package com.kingcontaria.standardsettings.mixins.accessors;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)

public interface MinecraftClientAccessor {
    @Accessor
    void setOpenProfilerSection(String value);
    @Accessor
    String getOpenProfilerSection();

    @Invoker
    void callInitFont(boolean forcesUnicode);
}
