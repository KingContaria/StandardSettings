package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.nbt.NbtCompound;
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

    @Inject(method = "saveWorld(Lnet/minecraft/world/level/LevelProperties;Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private void saveStandardoptionsTxt(LevelProperties nbt, NbtCompound par2, CallbackInfo ci) {
        if (!new File(worldDir, "standardoptions.txt").exists() && StandardSettings.lastUsedFile != null) {
            if (StandardSettings.fileLastModified != StandardSettings.lastUsedFile.lastModified()) {
                StandardSettings.LOGGER.warn("standardoptions.txt has been modified since it's been applied");
            }
            try {
                Files.copy(StandardSettings.lastUsedFile.toPath(), new File(worldDir, "standardoptions.txt").toPath(), StandardCopyOption.REPLACE_EXISTING);
                StandardSettings.lastUsedFile = null;
                StandardSettings.LOGGER.info("Saved standardoptions.txt to world file");
            } catch (IOException e) {
                StandardSettings.LOGGER.error("Failed to save standardoptions.txt to world file", e);
            }
        }
    }

}
