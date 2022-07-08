package com.kingcontaria.standardsettings.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)

public interface MinecraftClientAccessor {
    @Accessor
    void setOpenProfilerSection(String value);

    @Invoker
    void callInitFont(boolean forcesUnicode);
}
