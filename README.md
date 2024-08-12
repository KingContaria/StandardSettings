## StandardSettings

*developed by KingContaria*

*Note that this README is for StandardSettings 2.0. If you're using an earlier mod version because you're playing on a Minecraft version 2.0 hasn't been released for, you can find the relevant README [**here**](https://github.com/KingContaria/StandardSettings/blob/8ac15b0/README.md).*

### What does this Mod do?

This mod defaults settings to a standard set by the player when creating a new world.
It's designed to replace the much slower, much laggier and more inconsistent solution of doing it by macro.

### Setup

This mod requires Speedrun API. You can download StandardSettings, Speedrun API, as well as all other mods allowed for speedrunning, from https://mods.tildejustin.dev/.

### How can I edit my standardsettings?

*If you used StandardSettings before, you'd be used to the mod reading from `.../.minecraft/config/standardoptions.txt`. This is no longer the case!*

To get to the config screen, go to `Options > Book and Quill > StandardSettings` in game. Here you can edit all the settings changed by StandardSettings, including most settings from Minecraft's `options.txt` file, plus some additional settings. It does not include settings that aren't accessible from inside the game (i.e. difficulty – configure it in Atum's settings instead).

There is a global toggle for all settings at the top – setting "Toggle StandardSettings" to "OFF" will prevent every setting from being reset.

To find a specific setting quickly, press `Ctrl + F` in the config screen and start typing the name pf the setting.

To stop a specific setting from being reset, change the button on the right of it from "ON" to "OFF".

You do not need to restart the instance for new settings to take effect.

### How does it work?

The mod reads the `.../.minecraft/config/mcsr/standardsettings.json` file in the config folder of your Minecraft instance and defaults your settings to the values specified in that file every time a new world is created.

After that is done, it will also perform a quick check to make sure the values are within the boundaries of vanilla minecraft / the speedrun.com rule set.

### What are the additional settings?

Depending on the Minecraft version you are playing, some of these might not be included.

**F3 Pause On World Load**: This will use the F3+Esc pause screen instead of the regular pause screen for WorldPreview and for joining the world with Minecraft unfocused.

**First World F3 Pause Delay**: Sets a delay for F3 Pause on the first world to circumvent first world lag preventing chunks from rendering.

**Entity Culling**: Sets the Sodium "Entity Culling" option.

**Sneak Toggled**: If you are using togglesneak, this resets the state of sneaking, otherwise does nothing.

**Sprint Toggled**: If you are using togglesprint, this resets the state of sprinting, otherwise does nothing.

**Chunk Borders**: This resets if chunk borders are on or off.

**Hitboxes**: This resets if hitboxes are on or off.

**Perspective**: This resets what perspective you are in.

**Pie Directory**: This resets the directory your piechart is in. Common pie chart directories:
- Mapless / Preemptive: `root.gameRenderer.level.entities`
- Village / Fortress: `root.tick.level.entities.blockEntities`

To put them into standardsettings, you can copy the right pie directory, select the box, and press `Ctrl + A` > `Ctrl + V`.

**Hud Hidden (F1)**: This resets the state of F1 (GUI elements being hidden).

### On World Join settings

Since On World Join settings might be a bit unintuitive for people to use who aren't familiar with this mod, here is a quick summary:

Take '**Render Distance**' as example. The value you set '**Render Distance**' to under `Video Settings` is the value it gets changed to at the start of world generation. This will also be the value your background instances (in case you do multi instance) will be at, for example 5RD.

The value you set '**Render Distance**' to under `On World Join settings` is the value it gets set to either when you join the world or once you first focus the instance. This means you want to put this as the value you want to use while playing, for example 16RD.

The same concept applies for all the other OnWorldJoin options too. Here is a list of them:

**FOV**: This is the FOV the game will change to once you finish world creation & have the instance focused.

**Render Distance**: This is the Render Distance the game will change to once you finish world creation and have the instance focused. For Pre 1.9: this option can cause issues when not playing on wall and also just isn't that helpful for single instance anyway.

**Simulation Distance**: This is the Simulation Distance (1.18+) the game will change to once you finish world creation & have the instance focused.

**Entity Distance**: This is the Entity Distance (1.16+) the game will change to once you finish world creation & have the instance focused.

**GUI Scale**: This is the GUI Scale the game will change to once you finish world creation & have the instance focused.

**Trigger On Resize**: If you enable this, resizing the window of your instance will also result in the OnWorldJoin options being triggered. This is mainly useful for macros to implement delays between activating OnWorldJoin options and joining the instance.

### How can I use a global standardoptions file?

To do this, you have to create a `.minecraft/config/mcsr/standardsettings.global` file and put a path to a global file there, for example:
```
C:\Users\KingContaria\Desktop\speedrunning stuff\standardoptions\globalstandardoptions_116.json
```
This will make StandardSettings use the settings specified there instead of `.minecraft/config/mcsr/standardsettings.json`, and will allow you to change the settings in this file in game from Speedrun API.

By putting a directory to another `.global` file into that file, you can now create chains of file paths. This can be useful if you use a lot of different settings for different categories.