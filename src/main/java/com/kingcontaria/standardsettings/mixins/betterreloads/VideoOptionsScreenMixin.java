package com.kingcontaria.standardsettings.mixins.betterreloads;

import com.kingcontaria.standardsettings.mixins.BakedModelManagerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.VideoOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.CompletableFuture;

@Mixin(VideoOptionsScreen.class)

public class VideoOptionsScreenMixin {

    @Redirect(method = "removed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;reloadResourcesConcurrently()Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<Void> reloadMipMapLevels(MinecraftClient client) {
        ((BakedModelManagerAccessor)client.getBakedModelManager()).callApply(((BakedModelManagerAccessor)client.getBakedModelManager()).callPrepare(client.getResourceManager(), client.getProfiler()), client.getResourceManager(), client.getProfiler());
        return null;
    }

}
