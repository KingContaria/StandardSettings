package me.contaria.standardsettings.mixin.accessors;

import net.minecraft.client.options.Option;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Option.class)
public interface OptionAccessor {
    @Accessor("key")
    String standardsettings$getKey();
}
