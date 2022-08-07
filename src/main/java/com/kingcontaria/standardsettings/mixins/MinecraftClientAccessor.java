package com.kingcontaria.standardsettings.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModelManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)

public interface MinecraftClientAccessor {
    @Accessor
    void setOpenProfilerSection(String value);
    @Accessor
    String getOpenProfilerSection();

    @Accessor
    BakedModelManager getModelManager();
}
