package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.util.Window;
import net.minecraft.world.level.LevelInfo;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
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

    private static boolean bl = true;
    @Shadow public abstract boolean isWindowFocused();

    @Shadow public boolean focused;

    @Inject(method = "initializeGame", at = @At("RETURN"))
    private void initializeStandardSettings(CallbackInfo ci) {
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
        List<String> lines = null;
        try {
            lines = com.google.common.io.Files.readLines(StandardSettings.standardoptionsFile, Charset.defaultCharset());
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
                    view.write("standardsettings", Charset.defaultCharset().encode(String.join(".", Arrays.stream(globalFileVersion).mapToObj(String::valueOf).toArray(String[]::new))));
                } catch (IOException e) {
                    StandardSettings.LOGGER.error("Failed to adjust standardoptions.txt version to global file version", e);
                }
            }
        }

        try {
            List<String> linesToAdd = StandardSettings.checkVersion(fileVersion, lines);
            if (linesToAdd != null) {
                com.google.common.io.Files.append(System.lineSeparator() + String.join(System.lineSeparator(), linesToAdd), globalFile != null ? globalFile : StandardSettings.standardoptionsFile, Charset.defaultCharset());
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

    @Inject(method = "startGame", at = @At("HEAD"))
    private void resetSettings(String fileName, String worldName, LevelInfo levelInfo, CallbackInfo ci) {
        if (!new File("saves", fileName).exists()) {
            if (bl) {
                StandardSettings.changeOnWindowActivation = false;
                StandardSettings.LOGGER.info("Reset to StandardSettings...");
                StandardSettings.load();
                StandardSettings.LOGGER.info("Checking Settings...");
                StandardSettings.checkSettings();
                StandardSettings.options.save();
                bl = false;
            }
        } else {
            StandardSettings.optionsCache.load(fileName);
        }
        StandardSettings.lastQuitWorld = fileName;
    }

    @Inject(method = "startGame", at = @At("RETURN"))
    private void onWorldJoin(String fileName, String worldName, LevelInfo levelInfo, CallbackInfo ci) {
        if (this.isWindowFocused()) {
            StandardSettings.changeSettingsOnJoin();
        } else {
            StandardSettings.changeOnWindowActivation = true;
        }
        bl = true;
    }

    @Inject(method = "runGameLoop", at = @At("HEAD"))
    private void changeSettingsOnJoin(CallbackInfo ci) {
        if (StandardSettings.changeOnWindowActivation && Display.isActive()) {
            StandardSettings.changeOnWindowActivation = false;
            StandardSettings.changeSettingsOnJoin();
        }
    }

}