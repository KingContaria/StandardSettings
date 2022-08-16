package com.kingcontaria.standardsettings.mixins;

import com.kingcontaria.standardsettings.StandardSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.level.LevelInfo;
import org.lwjgl.opengl.Display;
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
import java.util.*;
import java.util.stream.Stream;

@Mixin(MinecraftClient.class)

public class MinecraftClientMixin {

    private static boolean bl = true;

    @Inject(method = "initializeGame", at = @At("RETURN"))
    private void initializeStandardSettings(CallbackInfo ci) {
        if (!StandardSettings.standardoptionsFile.exists()) {
            UserDefinedFileAttributeView view = Files.getFileAttributeView(StandardSettings.standardoptionsFile.toPath(), UserDefinedFileAttributeView.class);
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

        Map<UserDefinedFileAttributeView, int[]> fileVersionsMap = new HashMap<>();
        List<String> lines = new ArrayList<>();
        try {
            lines = StandardSettings.resolveGlobalFile(StandardSettings.standardoptionsFile);
            for (File file : StandardSettings.filesLastModifiedMap.keySet()) {
                UserDefinedFileAttributeView view = Files.getFileAttributeView(file.toPath(), UserDefinedFileAttributeView.class);
                fileVersionsMap.put(view, readVersion(view));
            }
        } catch (Exception e) {
            StandardSettings.LOGGER.error("Failed to check for file versions", e);
        }

        int[] highestVersion = new int[]{1,2,0,0};
        for (int[] fileVersion : fileVersionsMap.values()) {
            if (StandardSettings.compareVersions(highestVersion, fileVersion)) {
                highestVersion = fileVersion;
            }
        }

        try {
            List<String> linesToAdd = StandardSettings.checkVersion(highestVersion, lines);
            if (linesToAdd != null) {
                com.google.common.io.Files.append(System.lineSeparator() + String.join(System.lineSeparator(), linesToAdd), StandardSettings.lastUsedFile, Charset.defaultCharset());
                StandardSettings.LOGGER.info("Finished updating standardoptions.txt");
            }
            for (Map.Entry<UserDefinedFileAttributeView, int[]> entry : fileVersionsMap.entrySet()) {
                if (StandardSettings.compareVersions(entry.getValue(), StandardSettings.version)) {
                    try {
                        entry.getKey().write("standardsettings", Charset.defaultCharset().encode(StandardSettings.getVersion()));
                    } catch (IOException e) {
                        StandardSettings.LOGGER.error("Failed to sign version number to file", e);
                    }
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
        if (Display.isActive()) {
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
