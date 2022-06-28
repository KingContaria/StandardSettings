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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Mixin(WorldSaveHandler.class)

public class WorldSaveHandlerMixin {

    @Shadow @Final private File worldDir;

    @Inject(method = "saveWorld(Lnet/minecraft/world/level/LevelProperties;Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void saveOptionsTxt(LevelProperties levelProperties, CompoundTag compoundTag, CallbackInfo ci) {
        try {
            Files.copy(StandardSettings.optionsFile.toPath(), new File(worldDir, "options.txt").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            StandardSettings.LOGGER.error("Couldn't save options.txt to world file", e);
        }
        File optifineOptions = new File("optionsof.txt");
        if (!optifineOptions.exists()) {
            return;
        }
        try {
            Files.copy(optifineOptions.toPath(), new File(worldDir, "optionsof.txt").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            StandardSettings.LOGGER.error("Couldn't save optionsof.txt to world file", e);
        }
    }

}
