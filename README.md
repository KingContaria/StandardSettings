[not allowed]

Resetting Mod for Minecraft Speedrunning

This Mod will set your Settings to a preset standard when creating a new world. It will also check if your settings are within the allowed boundaries of the game / the speedrun.com rules.

To set your standard settings, click the Book and Quill Button on the Titlescreen, this will create a 'standardoptions.txt' file in your Minecraft directory. If you hold shift while clicking the button, it will open up the 'standardoptions.txt' file.

It will be a copy of your current options.txt file plus some additional features:

    perspective:0 (0 = first person, 1 = from the back, 2 = from the front)
    piedirectory:root.tick.level.entities.blockEntities (Path of your Piechart)
    hitboxes:true (Show/hide Hitboxes)

It will also add the following settings into the file to configure, that will determine what settings it gets set to once you join the world/instance, leave blank to use the normal standardsettings:

    renderDistanceOnWorldJoin:
    fovOnWorldJoin:

If you don't want some settings to be reset you have to manually remove the line from the file.
