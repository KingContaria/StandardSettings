package com.kingcontaria.standardsettings.mixins.betterreloads;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net/minecraft/client/class_381")

public class LanguageOptionsScreenMixin {

    @Redirect(method = "selectEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;stitchTextures()V"))
    private void replaceStitching(MinecraftClient client) {
        client.getLanguageManager().reload(client.getResourceManager());
    }

}
