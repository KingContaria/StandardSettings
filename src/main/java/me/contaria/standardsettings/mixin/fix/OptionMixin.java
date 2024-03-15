package me.contaria.standardsettings.mixin.fix;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import me.contaria.standardsettings.StandardGameOptions;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.NarratorOption;
import net.minecraft.client.options.Option;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Option.class)
public abstract class OptionMixin {

    @WrapWithCondition(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;reload()V"))
    private static boolean doNotReloadRenderer(WorldRenderer worldRenderer, @Local(argsOnly = true) GameOptions options) {
        return !(options instanceof StandardGameOptions);
    }

    @WrapWithCondition(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;scheduleTerrainUpdate()V"))
    private static boolean doNotUpdateRenderer(WorldRenderer worldRenderer, @Local(argsOnly = true) GameOptions options) {
        return !(options instanceof StandardGameOptions);
    }

    @WrapWithCondition(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;reset()V"))
    private static boolean doNotResetChat(ChatHud chatHud, @Local(argsOnly = true) GameOptions options) {
        return !(options instanceof StandardGameOptions);
    }

    @WrapWithCondition(method = "method_18559", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;setFramerateLimit(I)V"))
    private static boolean doNotSetFPS(Window window, int framerateLimit, @Local(argsOnly = true) GameOptions options) {
        return !(options instanceof StandardGameOptions);
    }

    @WrapWithCondition(method = "method_18536", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/NarratorManager;addToast(Lnet/minecraft/client/options/NarratorOption;)V"))
    private static boolean doNotAddToast(NarratorManager manager, NarratorOption option, @Local(argsOnly = true) GameOptions options) {
        return !(options instanceof StandardGameOptions);
    }

    @ModifyExpressionValue(method = {"method_18579", "method_18570", "method_18529", "method_21669"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getWindow()Lnet/minecraft/client/util/Window;"))
    private static Window doNotModifyWindow(Window window, @Local(argsOnly = true) GameOptions options) {
        if (options instanceof StandardGameOptions) {
            return null;
        }
        return window;
    }
}
