package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Mixin(MinecraftClient.class)

public abstract class MinecraftClientMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initializeStandardSettings(RunArgs args, CallbackInfo ci) {
        UserDefinedFileAttributeView view = Files.getFileAttributeView(StandardSettings.standardoptionsFile.toPath(), UserDefinedFileAttributeView.class);
        if (!StandardSettings.standardoptionsFile.exists()) {
            StandardSettings.LOGGER.info("Creating StandardSettings File...");

            long start = System.nanoTime();

            if (!StandardSettings.standardoptionsFile.getParentFile().exists()) {
                StandardSettings.standardoptionsFile.getParentFile().mkdir();
            }

            try {
                Files.write(StandardSettings.standardoptionsFile.toPath(), StandardSettings.getStandardoptionsTxt().getBytes());
                view.write("standardsettings", Charset.defaultCharset().encode(StandardSettings.getVersion()));
                StandardSettings.LOGGER.info("Finished creating StandardSettings File ({} ms)", (System.nanoTime() - start) / 1000000.0f);
            } catch (IOException e) {
                StandardSettings.LOGGER.error("Failed to create StandardSettings File", e);
            }
            return;
        }

        File globalFile = null;
        UserDefinedFileAttributeView globalFileView = null;
        try {
            List<String> lines = com.google.common.io.Files.readLines(StandardSettings.standardoptionsFile, Charset.defaultCharset());
            if (lines.size() > 0) {
                String firstLine = com.google.common.io.Files.readLines(StandardSettings.standardoptionsFile, Charset.defaultCharset()).get(0);
                globalFile = new File(firstLine);
                if (!globalFile.exists()) {
                    globalFile = null;
                } else {
                    globalFileView = Files.getFileAttributeView(globalFile.toPath(), UserDefinedFileAttributeView.class);
                }
            }
        } catch (IOException e) {
            StandardSettings.LOGGER.error("Failed to check for global file", e);
        }

        int[] fileVersion = readVersion(view);
        if (globalFile != null) {
            int[] globalFileVersion = readVersion(globalFileView);
            if (StandardSettings.compareVersions(fileVersion, globalFileVersion)) {
                fileVersion = globalFileVersion;
                try {
                    view.write("standardsettings", Charset.defaultCharset().encode(System.lineSeparator() + String.join(".", Arrays.stream(globalFileVersion).mapToObj(String::valueOf).toArray(String[]::new))));
                } catch (IOException e) {
                    StandardSettings.LOGGER.error("Failed to adjust standardoptions.txt version to global file version", e);
                }
            }
        }

        try {
            String[] linesToAdd = StandardSettings.checkVersion(fileVersion);
            if (linesToAdd != null) {
                com.google.common.io.Files.append(String.join(System.lineSeparator(), linesToAdd), globalFile != null ? globalFile : StandardSettings.standardoptionsFile, Charset.defaultCharset());
                StandardSettings.LOGGER.info("Finished updating standardoptions.txt");
            }
            if (StandardSettings.compareVersions(fileVersion, StandardSettings.version)) {
                view.write("standardsettings", Charset.defaultCharset().encode(StandardSettings.getVersion()));
                if (globalFile != null) {
                    globalFileView.write("standardsettings", Charset.defaultCharset().encode(StandardSettings.getVersion()));
                }
            }
        } catch (IOException e) {
            StandardSettings.LOGGER.error("Failed to update standardoptions.txt", e);
        }
    }

    private int[] readVersion(UserDefinedFileAttributeView view) {
        try {
            String name = "standardsettings";
            ByteBuffer buf = ByteBuffer.allocate(view.size(name));
            view.read(name, buf);
            buf.flip();
            String value = Charset.defaultCharset().decode(buf).toString();
            return Stream.of(value.split("\\.")).mapToInt(Integer::parseInt).toArray();
        } catch (IOException | IllegalArgumentException e) {
            return new int[]{1,2,0,0};
        }
    }

    @Inject(method = "onWindowFocusChanged", at = @At("RETURN"))
    private void changeSettingsOnJoin(boolean focused, CallbackInfo ci) {
        if (focused && StandardSettings.changeOnWindowActivation) {
            StandardSettings.changeOnWindowActivation = false;
            StandardSettings.changeSettingsOnJoin();
        }
    }

    @Inject(method = "onResolutionChanged", at = @At("HEAD"))
    private void changeSettingsOnResize(CallbackInfo ci) {
        if (StandardSettings.changeOnWindowActivation && StandardSettings.changeOnResize) {
            StandardSettings.changeOnWindowActivation = false;
            StandardSettings.changeSettingsOnJoin();
        }
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    private void cacheOptions(CallbackInfo ci) {
        StandardSettings.changeOnWindowActivation = false;
        try {
            StandardSettings.lastQuitWorld = StandardSettings.client.getServer().getIconFile().getParentFile().getName();
        } catch (Exception e) {
            // empty catch block
        }
    }

}
