package com.kingcontaria.standardsettings.mixins.accessors;

import net.minecraft.client.texture.ITexturePack;
import net.minecraft.client.texture.TexturePackManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TexturePackManager.class)
public interface TexturePackManagerAccessor {
    @Accessor("DEFAULT_PACK")
    static ITexturePack getDefaultPack() {
        throw new UnsupportedOperationException();
    }
}
