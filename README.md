###### ***NOT ALLOWED (yet pauseman)***

## StandardSettings

*developed by KingContaria*

### What does this Mod do?

This Mod defaults settings to a standard set by the player when creating a new World.
It's designed to replace the much slower, much laggier and more inconsistent solution of doing it by macro.

### How does it work?

At the start of creating a new world, the Mod will check for a 'standardoptions.txt' file in your config folder.

It now defaults your settings to the values specified in that file. After that is done, it will also perform a quick check to make sure the values are within the boundaries of vanilla minecraft / the speedrun.com ruleset.

### How can I edit my standardsettings?

When first launching Minecraft with this mod (or whenever there is no standardoptions.txt in your config folder), a standardoptions file based on your current settings will be created.

This file contains all the settings changed by StandardSettings, including most settings from options.txt file plus some additional settings. It does not include settings that aren't accessible from ingame and some unnecessary stuff (for example multiplayer related options).

To change your settings, you can either manually edit them in the file or set your settings ingame, then delete the file and restart Minecraft to generate a new one.

To stop a setting from being reset, remove the corresponding line from the file.

### What are the additional settings?

Depending on the version you are playing some of these might not be included.

**sneaking**: If you are using togglesneak, this resets the state of sneaking. (Set to 'true' or 'false')

**sprinting**: If you are using togglesprint, this resets the state of sprinting. (Set to 'true' or 'false')

**chunkborders**: This resets if chunk borders are on or off. (Set to 'true' or 'false')

**hitboxes**: This resets if hitboxes are on or off. (Set to 'true' or 'false')

**perspective**: This resets what perspective you are in. (0 = first person, 1 = third person back, 2 = third person front)

**piedirectory**: This resets the directory your piechart is in. Spelling is key, check upper/lower case! (common directories [here](#common-piechart-directories))

**fovOnWorldJoin**: This is the FOV the game will change to once you finish world creation & have the instance focused.

**renderDistanceOnWorldJoin**: This is the Render Distance the game will change to once you finish world creation & have the instance focused.

**simulationDistanceOnWorldJoin**: This is the Simulation Distance (1.18+) the game will change to once you finish world creation & have the instance focused.

**entityDistanceScalingOnWorldJoin**: This is the Entity Distance (1.16+) the game will change to once you finish world creation & have the instance focused.

### How can I use a global standardoptions file?

To do this, you have to replace the text in the 'standardoptions.txt' of all your instances to the file directory of that global file, for example:

C:\Users\KingContaria\Desktop\speedrunning stuff\standardoptions\globalstandardoptions_116.txt

#

### Common piechart directories

**mapless / preemptive**: root.gameRenderer.level.entities
**blockentities**: root.tick.level.entities.blockEntities