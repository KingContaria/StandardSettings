package com.kingcontaria.standardsettings.mixins;

import net.minecraft.client.resource.language.LanguageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(LanguageManager.class)
public interface LanguageManagerAccessor {
    @Accessor
    Map getField_6653();
}
