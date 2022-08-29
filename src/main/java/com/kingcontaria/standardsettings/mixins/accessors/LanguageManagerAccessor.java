package com.kingcontaria.standardsettings.mixins.accessors;

import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(LanguageManager.class)
public interface LanguageManagerAccessor {
    @Accessor
    Map<String, LanguageDefinition> getLanguageDefs();
}
