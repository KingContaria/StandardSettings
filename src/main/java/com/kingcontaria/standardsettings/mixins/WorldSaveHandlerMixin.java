package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(WorldSaveHandler.class)

public class WorldSaveHandlerMixin {

    @Shadow @Final private File worldDir;

    @Inject(method = "saveWorld(Lnet/minecraft/world/level/LevelProperties;Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void saveOptionsTxt(LevelProperties levelProperties, CompoundTag compoundTag, CallbackInfo ci){
        StandardSettings.save(new File(this.worldDir,"options.txt"));
    }

}
