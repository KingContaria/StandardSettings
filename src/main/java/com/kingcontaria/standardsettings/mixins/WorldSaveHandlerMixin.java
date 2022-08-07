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

@Mixin(WorldSaveHandler.class)

public class WorldSaveHandlerMixin {

    @Shadow @Final private File worldDir;
    private final File file = new File(worldDir, "standardoptions.txt");

    @Inject(method = "saveWorld(Lnet/minecraft/world/level/LevelProperties;Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void saveStandardoptionsTxt(LevelProperties levelProperties, CompoundTag compoundTag, CallbackInfo ci) {
        if (!file.exists() && StandardSettings.standardoptionsCache != null) {
            try {
                Files.write(worldDir.toPath().resolve("standardoptions.txt"), String.join(System.lineSeparator(), StandardSettings.standardoptionsCache).getBytes());
                StandardSettings.LOGGER.info("Saved standardoptions.txt to world file");
            } catch (IOException e) {
                StandardSettings.LOGGER.error("Failed to save standardoptions.txt to world file", e);
            }
        }
    }

}
