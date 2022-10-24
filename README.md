## StandardSettings

*developed by KingContaria*

### What does this Mod do?

This Mod defaults settings to a standard set by the player when creating a new World.
It's designed to replace the much slower, much laggier and more inconsistent solution of doing it by macro.

### How does it work?

The Mod reads the 'standardoptions.txt' file in the config folder of your Minecraft instance and every time you create a new world it will default your settings to the values specified in that file. 

After that is done, it will also perform a quick check to make sure the values are within the boundaries of vanilla minecraft / the speedrun.com rule set.

You can change the values in the file whenever you want, the mod will detect that it has been edited and reread the file.

### How can I edit my standardsettings?

When first launching Minecraft with this mod (or whenever there is no 'standardoptions.txt' in your config folder), a standardoptions file based on your current settings will be created.

This file contains all the settings changed by StandardSettings, including most settings from Minecrafts options.txt file plus some additional settings. It does not include settings that aren't accessible from inside the game and some unnecessary ones (for example multiplayer related options) are also left out.

To change your settings, you can either manually edit them in the file or set your settings ingame, then delete the file and restart Minecraft to generate a new one.

To stop a setting from being reset, remove the corresponding line from the file or leave the value behind the ':' blank.

### What are the additional settings?

Depending on the Minecraft version you are playing some of these might not be included.

**entityCulling**: Sets the sodium option Entity Culling. (Set to 'true' or 'false')

**sneaking**: If you are using togglesneak, this resets the state of sneaking. (Set to 'true' or 'false')

**sprinting**: If you are using togglesprint, this resets the state of sprinting. (Set to 'true' or 'false')

**chunkborders**: This resets if chunk borders are on or off. (Set to 'true' or 'false')

**hitboxes**: This resets if hitboxes are on or off. (Set to 'true' or 'false')

**perspective**: This resets what perspective you are in. (0 = first person, 1 = third person back, 2 = third person front)

**piedirectory**: This resets the directory your piechart is in. Spelling is key, check upper/lower case! (common directories [here](#common-piechart-directories))

**f1**: This resets the state of F1 (GUI elements being hidden). (Set to 'true' or 'false')

**fovOnWorldJoin**: This is the FOV the game will change to once you finish world creation & have the instance focused. You can set this value as either 30 to 110 as in the ingame menu or -1.0 to 1.0 as in options.txt, both will work. More Info on OnWorldJoin options [here](#onworldjoin-options).

**guiScaleOnWorldJoin**: This is the GUI Scale the game will change to once you finish world creation & have the instance focused.

**renderDistanceOnWorldJoin**: This is the Render Distance the game will change to once you finish world creation & have the instance focused. For Pre 1.9: This option can cause issues when not playing on wall and also just isn't that helpful for single instance anyway.

**simulationDistanceOnWorldJoin**: This is the Simulation Distance (1.18+) the game will change to once you finish world creation & have the instance focused.

**entityDistanceScalingOnWorldJoin**: This is the Entity Distance (1.16+) the game will change to once you finish world creation & have the instance focused.

**changeOnResize**: If you set this to true, resizing the window of your instance will also result in the OnWorldJoin options being triggered. This is mainly useful for macros to implement delays between activating OnWorldJoin options and joining the instance.

**skipWhenPossible**: If this is set to true, settings won't be reset when the last world was reset in WorldPreview or was never focused. The setting is enabled by default and there is no real reason to turn it off. If you change your standardoptions.txt, that will still be detected.

### How can I use a global standardoptions file?

To do this, you have to replace the text in the 'standardoptions.txt' of all your instances to the file directory of that global file, for example:

C:\Users\KingContaria\Desktop\speedrunning stuff\standardoptions\globalstandardoptions_116.txt

By putting another file directory into that file, you can now create chains of file paths. This can be useful if you use a lot of different settings for different categories.

#

### Common piechart directories

**mapless / preemptive**: root.gameRenderer.level.entities

**blockentities**: root.tick.level.entities.blockEntities

#

### OnWorldJoin options

Since OnWorldJoin options might be a bit unintuitive for people to use who have used settings resetting in macros before, here is a quick summary:

We take as example renderDistanceOnWorldJoin. The value you enter into '**renderDistance:**' is the value it gets changed to at the start of world generation. This will also be the value your background instances (in case you do multi instance) will be at, for example 5RD.

The value you enter into '**renderDistanceOnWorldJoin:**' is the value it gets set to either when you join the world or once you first focus the instance. This means you want to put this as the value you want to use while playing, for example 16RD.

The same concept applies for all the other OnWorldJoin options too.