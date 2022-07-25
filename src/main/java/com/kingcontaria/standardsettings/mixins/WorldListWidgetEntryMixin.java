package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/client/gui/screen/world/WorldListWidget$Entry")

public class WorldListWidgetEntryMixin {

    @Shadow @Final private LevelSummary level;

    @Inject(method = "play", at = @At("HEAD"))
    private void loadCache(CallbackInfo ci) {
        StandardSettings.optionsCache.load(this.level.getName());
    }

}
