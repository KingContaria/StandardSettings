package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.client.class_2847;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_2847.class)

public class WorldListWidgetEntryMixin {

    @Shadow @Final private LevelSummary field_13365;

    @Inject(method = "method_12210", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;startGame(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/world/level/LevelInfo;)V"))
    private void loadCache(CallbackInfo ci) {
        StandardSettings.optionsCache.load(this.field_13365.getFileName());
    }

}
